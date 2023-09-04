package com.tk.mybatis.builder;

import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.ParameterMapping;
import com.tk.mybatis.mapping.SqlSource;
import com.tk.mybatis.session.Configuration;

import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/29 21:03
 * @Version 1.0
 * @Description 静态sql源码
 */
public class StaticSqlSource implements SqlSource {
    private String sql;
    private List<ParameterMapping> parameterMappings;
    private Configuration configuration;

    public StaticSqlSource(Configuration configuration,String sql){
        this(configuration,sql,null);
    }

    public StaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
        this.configuration = configuration;
    }


    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(configuration,sql,parameterMappings,parameterObject);
    }
}
