package com.tk.mybatis.transaction.jdbc;

import com.tk.mybatis.session.TransactionIsolationLevel;
import com.tk.mybatis.transaction.Transaction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author liuht
 * @Date 2023/8/2 19:53
 * @Version 1.0
 * @Description 描述
 */
public class JdbcTransaction implements Transaction {

    protected Connection connection;
    protected DataSource dataSource;
    protected boolean autoCommit;
    protected TransactionIsolationLevel level = TransactionIsolationLevel.NONE;

    public JdbcTransaction(DataSource dataSource,TransactionIsolationLevel level,
                                  boolean autoCommit){
        this.dataSource = dataSource;
        this.level = level;
        this.autoCommit = autoCommit;
    }

    public JdbcTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() throws SQLException {
        connection = dataSource.getConnection();
        connection.setAutoCommit(autoCommit);
        connection.setTransactionIsolation(level.getLevel());
        return connection;
    }

    @Override
    public void commit() throws SQLException {
        if(connection != null && connection.getAutoCommit()){
            commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void close() throws SQLException {
        System.out.println("关闭连接。。。");
        connection.close();
    }
}
