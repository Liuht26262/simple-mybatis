package com.tk.mybatis.mapping;

import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.type.JdbcType;
import com.tk.mybatis.type.TypeHandler;
import com.tk.mybatis.type.TypeHandlerRegistry;

/**
 * @Author liuht
 * @Date 2023/8/3 15:24
 * @Version 1.0
 * @Description 参数映射
 */
public class ParameterMapping {

    private Configuration configuration;

    private String property;

    private Class<?> javaType = Object.class;

    private JdbcType jdbcType;

    private TypeHandler typeHandler;

    public ParameterMapping(){};



    public static class Builder{
        private ParameterMapping parameterMapping = new ParameterMapping();

        public Builder(Configuration configuration,String property,Class<?> javaType){
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
            parameterMapping.javaType = javaType;
        }

        public Builder configuration(Configuration configuration){
            parameterMapping.configuration = configuration;
            return this;
        }

        public Builder property(String property){
            parameterMapping.property = property;
            return this;
        }

        public ParameterMapping build(){
            if(parameterMapping.typeHandler == null && parameterMapping.javaType != null){
                Configuration configuration = parameterMapping.configuration;
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                parameterMapping.typeHandler = typeHandlerRegistry.getTypeHandler(parameterMapping.javaType,parameterMapping.getJdbcType());
            }

            return parameterMapping;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getProperty() {
        return property;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public TypeHandler getTypeHandler() {
        return typeHandler;
    }
}
