package com.tk.mybatis.datasource.unPooled;

import com.tk.mybatis.io.Resources;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @Author liuht
 * @Date 2023/8/4 15:23
 * @Version 1.0
 * @Description 无池化数据库
 */
public class UnPooledDataSource implements DataSource {
    private final static org.slf4j.Logger log = LoggerFactory.getLogger(UnPooledDataSource.class);
    //驱动类加载器
    private ClassLoader driveClassLoad;
    //数据库连接url
    private String url;
    //用户名
    private String username;
    //驱动器全路径名
    private String driver;
    //密码
    private String password;
    //默认的事务级别
    private Integer defauleTransactionLevel;
    //驱动参数
    private Properties driverProperties;
    //驱动类Map
    private static Map<String, Driver> driverMap = new ConcurrentHashMap<>();
    //是否是自动提交
    private Boolean isAutoCommit;

    static{
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        if(drivers.hasMoreElements()){
            Driver driver = drivers.nextElement();
            driverMap.put(driver.getClass().getName(),driver);
        }
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
    public void setLogWriter(PrintWriter out) throws SQLException {
        DriverManager.setLogWriter(out);
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
        return null;
    }


    private static class DriverProxy implements Driver{
        private Driver driver;

        DriverProxy(Driver driver){
            this.driver = driver;
        }

        @Override
        public Connection connect(String url, Properties info) throws SQLException {
            return driver.connect(url, info);
        }

        @Override
        public boolean acceptsURL(String url) throws SQLException {
            return driver.acceptsURL(url);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
            return driver.getPropertyInfo(url, info);
        }

        @Override
        public int getMajorVersion() {
            return 0;
        }

        @Override
        public int getMinorVersion() {
            return 0;
        }

        @Override
        public boolean jdbcCompliant() {
            return false;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        }
    }


    @Override
    public Connection getConnection() throws SQLException {
        return doGetConnection(username,password);
    }

    private Connection doGetConnection(String username, String password) throws SQLException {
        Properties properties = new Properties();
        if(driverProperties != null){
            properties.putAll(driverProperties);
        }
        if(username != null){
            /**
             * 在JDBC连接配置中，用户名对应的键名是"user"，不是"username"
             * jdbc:mysql://localhost:3306/mydatabase?user=myusername&password=mypassword
             */
            properties.put("user",username);
        }
        if(password != null ){
            properties.put("password",password);
        }
        return doGetConnection(properties);
    }

    private Connection doGetConnection(Properties properties) throws SQLException {
        //初始化驱动器
        initializerDriver();
        Connection connection = DriverManager.getConnection(url, properties);
        if(isAutoCommit !=null && isAutoCommit!=connection.getAutoCommit()){
            connection.setAutoCommit(isAutoCommit);
        }
        if(defauleTransactionLevel != null){
            connection.setTransactionIsolation(defauleTransactionLevel);
        }

        return connection;
    }

    /**
     * 初始化驱动器
     * @throws SQLException
     */
    private synchronized void initializerDriver() throws SQLException {
        if(!driverMap.containsKey(driver)){
            try {
                Class<?> driverClass;
                if(driveClassLoad != null){
                    driverClass = Class.forName(driver, true, driveClassLoad);
                }else {
                    driverClass = Resources.getClass(driver);
                }
                Driver driverInstance = (Driver) driverClass.newInstance();
                //加入驱动器管理器
                DriverManager.registerDriver(new DriverProxy(driverInstance));
                driverMap.put(driver,driverInstance);
            } catch (Exception e) {
                throw new SQLException("Error setting driver on UnpooledDataSource. Cause: " + e);
            }
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return doGetConnection(username,password);
    }



    public ClassLoader getDriveClassLoad() {
        return driveClassLoad;
    }

    public void setDriveClassLoad(ClassLoader driveClassLoad) {
        this.driveClassLoad = driveClassLoad;
    }

    public String getUrl() {
        return url;
    }

    public synchronized void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public synchronized void setUsername(String username) {
        this.username = username;
    }

    public String getDriver() {
        return driver;
    }

    public synchronized void setDriver(String driver) {
        this.driver = driver;
    }

    public String getPassword() {
        return password;
    }

    public synchronized void setPassword(String password) {
        this.password = password;
    }

    public Integer getDefauleTransactionLevel() {
        return defauleTransactionLevel;
    }

    public void setDefauleTransactionLevel(Integer defauleTransactionLevel) {
        this.defauleTransactionLevel = defauleTransactionLevel;
    }

    public Properties getDriverProperties() {
        return driverProperties;
    }

    public void setDriverProperties(Properties driverProperties) {
        this.driverProperties = driverProperties;
    }

    public Map<String, Driver> getDriverMap() {
        return driverMap;
    }


    public Boolean getAutoCommit() {
        return isAutoCommit;
    }

    public void setAutoCommit(Boolean autoCommit) {
        isAutoCommit = autoCommit;
    }
}
