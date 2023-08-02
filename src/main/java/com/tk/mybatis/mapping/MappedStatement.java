package com.tk.mybatis.mapping;

import com.tk.mybatis.session.Configuration;

import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/7/31 23:12
 * @Version 1.0
 * @Description 映射语句类
 */
public class MappedStatement {
    /**
     * sql配置类
     */
    private Configuration configuration;
    /**
     * method对应的id
     */
    private String id;
    /**
     * 语句类型
     */
    private SqlCommandType sqlCommandType;
    /**
     * 参数类型
     */
    private String parameterType;
    /**
     * 结果集类型
     */
    private String resultType;
    /**
     * sql语句
     */
    private String sql;
    /**
     * 参数集合
     */
    private Map<Integer,String> parameter;

    public static class Builder{
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlCommandType sqlCommandType, String parameterType,
                       String resultType, String sql, Map<Integer, String> parameter) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.parameterType = parameterType;
            mappedStatement.resultType = resultType;
            mappedStatement.sql = sql;
            mappedStatement.parameter = parameter;
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            return mappedStatement;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public void setSqlCommandType(SqlCommandType sqlCommandType) {
        this.sqlCommandType = sqlCommandType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public void setParameterType(String parameterType) {
        this.parameterType = parameterType;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Map<Integer, String> getParameter() {
        return parameter;
    }

    public void setParameter(Map<Integer, String> parameter) {
        this.parameter = parameter;
    }
}
