package com.tk.mybatis.mapping;

import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/8/3 15:22
 * @Version 1.0
 * @Description 描述
 */
public class BoundSql {
    //sql语句
    private String sql;
    //入参
    private String parameterType;
    //出参
    private String resultType;
    //sql语句集合
    private Map<Integer,String> parameterMappins;

    public BoundSql(String sql, String parameterType, String resultType, Map<Integer, String> parameterMappins) {
        this.sql = sql;
        this.parameterType = parameterType;
        this.resultType = resultType;
        this.parameterMappins = parameterMappins;
    }

    public String getSql() {
        return sql;
    }

    public String getParameterType() {
        return parameterType;
    }

    public String getResultType() {
        return resultType;
    }

    public Map<Integer, String> getParameterMappins() {
        return parameterMappins;
    }
}
