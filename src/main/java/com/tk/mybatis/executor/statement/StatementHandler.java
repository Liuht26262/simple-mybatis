package com.tk.mybatis.executor.statement;

import sun.plugin2.main.server.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/9 21:24
 * @Version 1.0
 * @Description 语句处理器
 */
public interface StatementHandler {
    /**
     * 准备语句
     * @param connection
     * @return
     * @throws SQLException
     */
    Statement prepare(Connection connection) throws SQLException;

    /**
     * 参数化处理
     * @param statement
     * @throws SQLException
     */
    void parameterize(Statement statement) throws SQLException;

    /**
     * 执行查询
     * @param statement
     * @param resultHandler
     * @param <E>
     * @return
     * @throws SQLException
     */
    <E> List<E> query(Statement statement, ResultHandler resultHandler) throws SQLException;
}
