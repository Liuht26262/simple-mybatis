package com.tk.mybatis.session.defaults;


import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.session.SqlSession;
import com.tk.mybatis.session.SqlSessionFactory;

/**
 * @Author liuht
 * @Date 2023/7/29 23:09
 * @Version 1.0
 * @Description 描述
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration){
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
