package com.tk.mybatis.session.defaults;

import com.tk.mybatis.executor.Executor;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @Author liuht
 * @Date 2023/7/29 23:14
 * @Version 1.0
 * @Description 描述
 */
public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;
    private Executor executor;
    private static final Logger log = LoggerFactory.getLogger(DefaultSqlSession.class);

    public DefaultSqlSession(Configuration configuration,Executor executor){
        this.configuration = configuration;
        this.executor = executor;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你被代理了！" + "方法：" + statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedstatement = configuration.getMappedstatement(statement);
        List<T> list = executor.query(mappedstatement, parameter, Executor.NO_RESULT_HANDLER,
                mappedstatement.getBoundSql());

        return list.get(0);
    }


    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type,this);
    }

    @Override
    public Configuration getConfiguration(){
        return this.configuration;
    }

    @Override
    public void close() throws IOException{

    }
}
