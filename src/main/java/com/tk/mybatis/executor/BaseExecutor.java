package com.tk.mybatis.executor;

import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.transaction.Transaction;
import sun.plugin2.main.server.ResultHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/9 21:04
 * @Version 1.0
 * @Description 基础执行器
 */
public abstract class BaseExecutor implements Executor{
    protected Configuration configuration;
    protected Transaction transaction;
    protected Executor executor;

    private boolean closed;

    public BaseExecutor(Configuration configuration, Transaction transaction) {
        this.configuration = configuration;
        this.transaction = transaction;
        this.executor = this;
    }

    @Override
    public <E> List<E> query(MappedStatement statement, Object parameter, ResultHandler handler, BoundSql boundSql){
        if(closed){
            throw new RuntimeException("Executor was closed");
        }
        return doQuery(statement,parameter,handler,boundSql);
    }

    protected abstract <E> List<E> doQuery(MappedStatement statement, Object parameter,
                                           ResultHandler handler, BoundSql boundSql);

    @Override
    public void commit(boolean required) throws SQLException {
        if(closed){
            throw new RuntimeException("Cannot commit,transaction was close");
        }
        
        if (required){
            transaction.commit();
        }
    }

    @Override
    public Transaction getTransaction() {
        if(closed){
            throw new RuntimeException("Executor was closed");
        }
        return transaction;
    }

    @Override
    public void rollback(boolean reqired) throws SQLException {
        if(!closed){
            if(reqired){
                transaction.rollback();
            }
        }
    }

    @Override
    public void close(boolean required) {
        try {
            try{
                rollback(required);
            }finally {
                transaction.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            transaction = null;
            closed = true;
        }
    }
}
