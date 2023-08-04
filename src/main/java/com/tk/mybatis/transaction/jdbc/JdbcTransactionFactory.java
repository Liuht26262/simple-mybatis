package com.tk.mybatis.transaction.jdbc;

import com.tk.mybatis.session.TransactionIsolationLevel;
import com.tk.mybatis.transaction.Transaction;
import com.tk.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @Author liuht
 * @Date 2023/8/2 19:53
 * @Version 1.0
 * @Description 描述
 */
public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(Connection connection) {
        return new JdbcTransaction(connection);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }
}
