package com.tk.mybatis.executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/9 21:57
 * @Version 1.0
 * @Description 结果集处理器
 */
public interface ResultSetHandler {
    public <E> List<E> handleResultSet(Statement statement) throws SQLException;
}
