package com.tk.mybatis.mapping;

import com.tk.mybatis.scripting.LanguageDriver;
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

    private SqlSource sqlSource;

    Class<?> resultType;

    private LanguageDriver languageDriver;

    MappedStatement(){ };

    public Class<?> getResultType() {
        return resultType;
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public LanguageDriver getLang() {
        return languageDriver;
    }

    public static class Builder{
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlCommandType sqlCommandType, SqlSource sqlSource,Class<?> resultType) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.sqlSource = sqlSource;
            mappedStatement.resultType = resultType;
            mappedStatement.languageDriver = configuration.getLanguageRegistry().getDefaultDriver();
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

}
