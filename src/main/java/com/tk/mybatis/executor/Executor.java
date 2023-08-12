package com.tk.mybatis.executor;

import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.transaction.Transaction;
import sun.plugin2.main.server.ResultHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/9 20:21
 * @Version 1.0
 * @Description 执行器接口
 */
public interface Executor {
    ResultHandler NO_RESULT_HANDLER = null;

    /**
     * 查询方法
     * @param statement 映射语句
     * @param parameter 参数
     * @param handler   结果集处理器
     * @param boundSql  sql
     * @param <E>   结果集类型
     * @return
     */
    <E> List<E> query(MappedStatement statement, Object parameter, ResultHandler handler, BoundSql boundSql);

    /**
     * 获取事务
     * @return
     */
    Transaction getTransaction();

    /**
     * 提交事务
     * @param required
     */
    void commit(boolean required) throws SQLException;

    /**
     * 回滚事务
     * @param reqired
     */
    void rollback(boolean reqired) throws SQLException;

    /**
     * 关闭事务
     * @param required
     */
    void close(boolean required);

}
