package com.tk.mybatis.session.defaults;

import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.session.SqlSession;

/**
 * @Author liuht
 * @Date 2023/7/29 23:14
 * @Version 1.0
 * @Description 描述
 */
public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration){
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你被代理了！" + "方法：" + statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedstatement = configuration.getMappedstatement(statement);
        return (T) ("你被代理了！" + "方法：" + statement + " 入参：" + parameter+"要执行的sql: "+mappedstatement.getSql());
    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type,this);
    }

    @Override
    public Configuration getConfiguration(){
        return this.configuration;
    }
}
