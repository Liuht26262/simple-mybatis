package com.tk.mybatis.datasource.Pooled;

import com.tk.mybatis.datasource.unPooled.UnPooledDataSource;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.logging.Logger;


/**
 * @Author liuht
 * @Date 2023/8/7 19:13
 * @Version 1.0
 * @Description 描述
 */
public class PooledDataSource implements DataSource {
    private final static org.slf4j.Logger log = LoggerFactory.getLogger(PooledDataSource.class);

    private final PoolState state = new PoolState(this);

    private final UnPooledDataSource unPooledDataSource;

    // 活跃连接数
    protected int poolMaximumActiveConnections = 10;
    // 空闲连接数
    protected int poolMaximumIdleConnections = 5;
    // 在被强制返回之前,池中连接被检查的时间
    protected int poolMaximumCheckoutTime = 20000;
    // 这是给连接池一个打印日志状态机会的低层次设置,还有重新尝试获得连接, 这些情况下往往需要很长时间 为了避免连接池没有配置时静默失败)。
    protected int poolTimeToWait = 20000;
    // 发送到数据的侦测查询,用来验证连接是否正常工作,并且准备 接受请求。默认是“NO PING QUERY SET” ,这会引起许多数据库驱动连接由一 个错误信息而导致失败
    protected String poolPingQuery = "NO PING QUERY SET";
    // 开启或禁用侦测查询
    protected boolean poolPingEnabled = false;
    // 用来配置 poolPingQuery 多次时间被用一次
    protected int poolPingConnectionsNotUsedFor = 0;



    private int expectedConnectionTypeCode;

    public PooledDataSource() {
        this.unPooledDataSource = new UnPooledDataSource();
    }

    /**
     * 将连接放回连接池中
     * @param connection
     * @throws SQLException
     */
    public void pushConnection(PooledConnection connection) throws SQLException {
        state.activeConnections.remove(connection);
        synchronized (state){
            //验证连接的有效性
            log.info("空闲连接池数量[{}]",state.idleConnections.size());
            if(connection.isValid()){
                //如果空闲的连接小于设定的数量，需要创建新的连接加入到连接池中
                if(state.idleConnections.size() < poolMaximumIdleConnections && connection.getConnectionTypeCode() == expectedConnectionTypeCode){
                    state.accumulatedCheckoutTime += connection.getCheckoutTime();
                    if(!connection.getRealConnection().getAutoCommit()){
                        connection.getRealConnection().rollback();
                    }
                    //创建一个新的连接加入到连接池中
                    PooledConnection pooledConnection = new PooledConnection(connection.getRealConnection(), this);
                    state.idleConnections.add(pooledConnection);
                    pooledConnection.setCreatedTimestamp(connection.getCreatedTimestamp());
                    pooledConnection.setLastUsedTimestamp(connection.getLastUsedTimestamp());
                    //创建了新的对象之后，就将原始的连接对象标记为无效并回收
                    connection.invalidate();
                    log.info("Add connection "+ pooledConnection.getRealConnection() + " to Pool");

                    //通知其他线程可以竞争Connection了
                    state.notifyAll();
                }
                //空闲的连接比较充足
                else{
                    state.accumulatedCheckoutTime += connection.getCheckoutTime();
                    if(!connection.getRealConnection().getAutoCommit()){
                        connection.getRealConnection().rollback();
                    }

                    //将connection进行关闭并标记为无效并回收
                    connection.getRealConnection().close();
                    log.info("Closed connection "+connection.getRealHashCode());
                    connection.invalidate();
                }
            }else {
                //连接无效
                log.info("A bad connection (" + connection.getRealHashCode() + ") attempted to return to the pool, discarding connection.");
                state.badConnectionCount++;
            }
        }
    }

    /**
     * 获取连接
     * @param username
     * @param password
     * @return
     */
    private PooledConnection popConnection(String username,String password) throws SQLException {
        boolean countedWait = false;
        PooledConnection connection = null;
        long t = System.currentTimeMillis();
        int localBadConnectionCount = 0;

        while(connection == null){
            //锁定连接池
            synchronized (state){
                //如果空闲的池里面有就直接返回第一个
                if(!state.idleConnections.isEmpty()){
                    connection = state.idleConnections.remove(0);
                    log.info("Checked out connection "+connection.getRealConnection() + " from pool");
                }

                //如果没有就创建新的连接
                else{
                    log.info("活跃连接池数量[{}]",state.activeConnections.size());
                    //如果活跃连接数量不足
                    if(state.activeConnections.size()<poolMaximumActiveConnections){
                        //创建新的连接,这里的是真实连接
                        connection = new PooledConnection(unPooledDataSource.getConnection(), this);
                        log.info("Created Connection "+connection.getRealConnection());
                    }

                    //活跃连接数已满
                    else {
                        //获取活跃连接池中最老的一个连接，查看其连接时间是否超过最大等待时间
                        PooledConnection activeConnection = state.activeConnections.get(0);
                        long checkoutTime = activeConnection.getCheckoutTime();
                        //如果checkoutTime时间过长，就将其标记为过期
                        if(checkoutTime > poolMaximumCheckoutTime){
                            state.claimedOverdueConnectionCount++;
                            state.accumulatedCheckoutTimeOfOverdueConnections += checkoutTime;
                            state.accumulatedCheckoutTime += checkoutTime;
                            //移除该连接
                            state.activeConnections.remove(activeConnection);
                            if(!activeConnection.getRealConnection().getAutoCommit()){
                                activeConnection.getRealConnection().rollback();
                            }

                            //创建一个新的连接
                            connection = new PooledConnection(activeConnection.getRealConnection(),this);
                            //将老的连接标记为废弃
                            activeConnection.invalidate();
                            log.info("Claimed overdue connection " + activeConnection.getRealHashCode());
                        }

                        //checkoutTime超时时间不够长，就继续等待
                        else {
                            if (!countedWait){
                                state.hadToWaitCount++;
                                countedWait = true;
                            }
                            try {
                                log.info("Waiting as long as "+poolTimeToWait+ " milliseconds for connection");
                                long l = System.currentTimeMillis();
                                state.wait(poolTimeToWait);
                                state.accumulatedCheckoutTime = System.currentTimeMillis() - l;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                //TODO 获得连接后需要进行有效性校验
                //进行有效性校验
                if(connection != null){
                    if(connection.isValid()){
                        //将连接恢复到初始状态
                        if(!connection.getRealConnection().getAutoCommit()){
                            connection.getRealConnection().rollback();
                        }
                        //设置连接编码
                        connection.setConnectionTypeCode(assembleConnectionTypeCode(unPooledDataSource.getUrl()
                                ,username,password));
                        connection.setCheckoutTimestamp(System.currentTimeMillis());
                        connection.setLastUsedTimestamp(System.currentTimeMillis());
                        state.activeConnections.add(connection);
                        state.requestCount++;
                        state.accumulatedRequestTime += System.currentTimeMillis()-t;
                    }else {
                        //如果连接无效，表示连接异常
                        log.info("A bad connection (" + connection.getRealHashCode() + ") was returned from the pool," +
                                " getting another connection.");
                        // 如果没拿到，统计信息：失败链接 +1
                        state.badConnectionCount++;
                        localBadConnectionCount++;
                        connection = null;
                        // 失败次数较多，抛异常
                        if (localBadConnectionCount > (poolMaximumIdleConnections + 3)) {
                            log.debug("PooledDataSource: Could not get a good connection to the database.");
                            throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
                        }
                    }
                }
            }
        }
        if (connection == null){
            log.debug("PooledDataSource: Unknown severe error condition.  The connection pool returned a null connection.");
            throw new SQLException("PooledDataSource: Could not get a good connection to the database.");
        }

        return connection;
    }

    /**
     * 关闭连接池中所有连接
     */
    public void forceCloseAll(){
        synchronized (state){
            expectedConnectionTypeCode = assembleConnectionTypeCode(unPooledDataSource.getUrl(),
                    unPooledDataSource.getUsername(), unPooledDataSource.getPassword());
            //遍历活跃连接，逐个关闭
            for (int i = state.activeConnections.size(); i > 0 ; i--) {
                try {
                    PooledConnection activeConnection = state.activeConnections.remove(i - 1);
                    activeConnection.invalidate();
                    Connection realConnection = activeConnection.getRealConnection();
                    if (!realConnection.getAutoCommit()) {
                        realConnection.rollback();
                    }
                    realConnection.close();
                } catch (SQLException throwables) {
                    //忽略所有异常
                }
            }

            //遍历空闲连接，逐个关闭
            for (int i = state.idleConnections.size(); i > 0 ; i--) {
                try{
                    PooledConnection idleConnection = state.idleConnections.remove(i - 1);
                    idleConnection.invalidate();
                    Connection realConnection = idleConnection.getRealConnection();
                    if(!realConnection.getAutoCommit()){
                        realConnection.rollback();
                    }
                    realConnection.close();
                } catch (SQLException throwables) {
                    //忽略异常
                }
            }
        }
        log.info("All connections is closed in pooledDataSource");
    }

    /**
     * 通过代理连接返回真实连接
     * 用于对真实连接进行操作
     * @param connection
     * @return
     */
    public static Connection unWrapConnection(Connection connection){
        if(Proxy.isProxyClass(connection.getClass())){
            InvocationHandler invocationHandler = Proxy.getInvocationHandler(connection);
            if(invocationHandler instanceof PooledConnection){
                return ((PooledConnection)invocationHandler).getRealConnection();
            }
        }
        return connection;
    }

    private int assembleConnectionTypeCode(String url, String username, String password) {
        return (""+url+username+password).hashCode();
    }

    @Override
    protected void finalize() throws Throwable {
        forceCloseAll();
        super.finalize();
    }


    @Override
    public Connection getConnection() throws SQLException {
        return popConnection(unPooledDataSource.getUsername(), unPooledDataSource.getPassword()).getProxyConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return popConnection(username, password).getProxyConnection();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new SQLException(getClass().getName() + " is not a wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter logWriter) throws SQLException {
        DriverManager.setLogWriter(logWriter);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    }

    /**
     * 校验连接健康状态
     * @param pooledConnection
     * @return
     */
    public boolean pingConnection(PooledConnection pooledConnection) {
        boolean result = true;

        try{
            // 查看连接是否已经关闭
            log.info("连接[{}]的状态是[{}]",pooledConnection.getRealConnection().hashCode(),
                    !pooledConnection.getRealConnection().isClosed());

            result = !pooledConnection.getRealConnection().isClosed();
        } catch (SQLException throwables) {
            log.info("Connection "+pooledConnection.getRealHashCode()+" is BAD: "+ throwables.getMessage());
            result = false;
        }

        if(result){

            if(poolPingEnabled){
                //检测连接池中连接的健康状态
                //poolPingConnectionsNotUsedFor 表示连接距离上一次使用所等待的次数
                if(poolPingConnectionsNotUsedFor >= 0 && pooledConnection.getTimeElapsedSinceLastUse() > poolPingConnectionsNotUsedFor){
                    try{
                        log.info("Testing connection "+ pooledConnection.getRealConnection()+" ...");
                        Connection realConnection = pooledConnection.getRealConnection();
                        Statement statement = realConnection.createStatement();
                        ResultSet resultSet = statement.executeQuery(poolPingQuery);
                        resultSet.close();
                        if (!realConnection.getAutoCommit()){
                            realConnection.rollback();
                        }
                        result = true;
                        log.info("Connection "+pooledConnection.getRealHashCode()+" is GOOD");
                    } catch (SQLException throwables) {
                        log.info("Execution of ping query '"+ poolPingQuery + "' failed: "+throwables.getMessage());
                        try {
                            pooledConnection.getRealConnection().close();
                        } catch (SQLException e) {
                            //关闭资源的异常不用处理
                        }
                        result = false;
                        log.info("Connection "+pooledConnection.getRealHashCode()+" is BAD:"+throwables.getMessage());
                    }
                }
            }
        }
        return result;
    }



    public int getExpectedConnectionTypeCode() {
        return expectedConnectionTypeCode;
    }

    public void setExpectedConnectionTypeCode(int expectedConnectionTypeCode) {
        this.expectedConnectionTypeCode = expectedConnectionTypeCode;
    }

    public void setDriver(String driver) {
        unPooledDataSource.setDriverStr(driver);
        forceCloseAll();
    }

    public void setUrl(String url) {
        unPooledDataSource.setUrl(url);
        forceCloseAll();
    }

    public void setUsername(String username) {
        unPooledDataSource.setUsername(username);
        forceCloseAll();
    }

    public void setPassword(String password) {
        unPooledDataSource.setPassword(password);
        forceCloseAll();
    }
}
