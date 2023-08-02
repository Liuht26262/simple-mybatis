package com.tk.mybatis.session;

import com.tk.mybatis.builder.xml.XMLConfigBuilder;
import com.tk.mybatis.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

/**
 * @Author liuht
 * @Date 2023/7/31 16:48
 * @Version 1.0
 * @Description 描述
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader){
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        return build(xmlConfigBuilder.parser());
    }

    public SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}
