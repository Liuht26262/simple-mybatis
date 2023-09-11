package com.tk.mybatis.scripting.defaults;

import com.tk.mybatis.builder.SqlSourceBuilder;
import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.SqlSource;
import com.tk.mybatis.scripting.xmltags.DynamicContext;
import com.tk.mybatis.scripting.xmltags.SqlNode;
import com.tk.mybatis.session.Configuration;

import java.util.HashMap;

/**
 * @Author liuht
 * @Date 2023/8/28 20:49
 * @Version 1.0
 * @Description 原始sql源码 比DynamicSqlSource 动态sql处理快
 */

public class RawSqlSource implements SqlSource{

    private final SqlSource sqlSource;

    public RawSqlSource(Configuration configuration, SqlNode rootSqlNode, Class<?> parameterType){
        this(configuration,getSql(configuration, rootSqlNode),parameterType);
    }

    public RawSqlSource(Configuration configuration, String sql, Class<?> parameterType) {
        SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        Class<?> clazz = parameterType == null ? Object.class : parameterType;
        sqlSource = sqlSourceParser.parser(sql,clazz,new HashMap<>());
    }

    private static String getSql(Configuration configuration, SqlNode rootSqlNode) {
        DynamicContext context = new DynamicContext(configuration,null);
        rootSqlNode.apply(context);
        return context.getSql();
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }
}
