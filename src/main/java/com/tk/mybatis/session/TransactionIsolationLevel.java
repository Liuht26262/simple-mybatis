package com.tk.mybatis.session;

import java.sql.Connection;

/**
 * @Author liuht
 * @Date 2023/8/2 19:20
 * @Version 1.0
 * @Description 事务的隔离级别
 */
public enum TransactionIsolationLevel {
    //不支持事务
    NONE(Connection.TRANSACTION_NONE),
    //读已提交
    READ_COMMITTED(Connection.TRANSACTION_READ_COMMITTED),
    //读未提交
    READ_UNCOMMITTED(Connection.TRANSACTION_READ_UNCOMMITTED),
    //可重复读
    REPREATABLE_READ(Connection.TRANSACTION_REPEATABLE_READ),
    //串行化
    SERIALIZABLE(Connection.TRANSACTION_SERIALIZABLE),
    ;

    private final int level;


    TransactionIsolationLevel(int level) {
        this.level = level;
    }

    public int getLevel(){
        return this.level;
    }
}
