package com.tk.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author liuht
 * @Date 2023/8/2 16:19
 * @Version 1.0
 * @Description 描述
 */
public interface Transaction {

    /**
     * 获取数据库连接
     * @return
     */
    public Connection getConnection() throws SQLException;

    /**
     * 提交事务
     */
    public void commit() throws SQLException;

    /**
     * 事务回滚
     */
    public void rollback() throws SQLException;

    /**
     * 关闭事务
     */
    public void close() throws SQLException;
}
