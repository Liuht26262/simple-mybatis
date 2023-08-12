package com.tk.mybatis.executor.statement;

import com.tk.mybatis.executor.Executor;
import com.tk.mybatis.executor.resultset.ResultSetHandler;
import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.MappedStatement;
import sun.plugin2.main.server.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/9 21:51
 * @Version 1.0
 * @Description 描述
 */
public class SimpleStatementHandler extends BaseStatementHandler{
    public SimpleStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter, ResultHandler resultHandler, BoundSql boundSql) {
        super(executor, mappedStatement, parameter,resultHandler,boundSql);
    }

    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        return connection.createStatement();
    }

    @Override
    public void parameterize(Statement statement) throws SQLException {

    }

    @Override
    public <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException {
        String sql = boundSql.getSql();
        statement.execute(sql);
        return resultSetHandler.handleResultSet(statement);
    }
}
