package com.tk.mybatis.transaction;

import com.tk.mybatis.session.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @Author liuht
 * @Date 2023/8/2 16:19
 * @Version 1.0
 * @Description 事务工厂
 */
public interface TransactionFactory {
    /**
     * 创建新的事务
     * @param conn
     * @return
     */
    Transaction newTransaction(Connection conn);

    /**
     * 创建新的事务
     * @param dataSource    数据源信息
     * @param level     事务的隔离级别
     * @param autoCommit    是否自动提交
     * @return
     */
    Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level,boolean autoCommit);
}
