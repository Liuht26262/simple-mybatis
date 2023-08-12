package com.tk.mybatis.session.defaults;


import com.tk.mybatis.executor.Executor;
import com.tk.mybatis.mapping.Environment;
import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.session.SqlSession;
import com.tk.mybatis.session.SqlSessionFactory;
import com.tk.mybatis.session.TransactionIsolationLevel;
import com.tk.mybatis.transaction.Transaction;
import com.tk.mybatis.transaction.TransactionFactory;

import java.sql.SQLException;

/**
 * @Author liuht
 * @Date 2023/7/29 23:09
 * @Version 1.0
 * @Description 描述
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        Transaction transaction = null;
        try {
            Environment environment = configuration.getEnvironment();
            TransactionFactory transactionFactory = environment.getTransactionFactory();
            transaction = transactionFactory.newTransaction(environment.getDataSource(),
                    TransactionIsolationLevel.READ_COMMITTED, false);
            Executor executor = configuration.newExecutor(transaction);
            return new DefaultSqlSession(configuration, executor);
        } catch (Exception e) {
            try {
                if (transaction != null) {
                    transaction.close();
                }
            } catch (SQLException throwables) {
            }
            throw new RuntimeException("Cannot open session ,cause:"+e.getMessage());
        }
    }
}
