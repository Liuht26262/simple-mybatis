package com.tk.mybatis.executor;

import com.tk.mybatis.executor.statement.StatementHandler;
import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.transaction.Transaction;
import sun.plugin2.main.server.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/9 21:13
 * @Version 1.0
 * @Description 简易执行器
 */
public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement mappedStatement, Object parameter, ResultHandler handler,
                                  BoundSql boundSql) {
        Connection connection = null;
        try {
            Configuration configuration = mappedStatement.getConfiguration();
            StatementHandler statementHandler = configuration.newStatementHandler(this, mappedStatement,
                    parameter, handler, boundSql);
            connection = transaction.getConnection();
            Statement statement = statementHandler.prepare(connection);
            statementHandler.parameterize(statement);
            return statementHandler.query(statement, handler);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }
}
