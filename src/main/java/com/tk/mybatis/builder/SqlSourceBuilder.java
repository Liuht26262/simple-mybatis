package com.tk.mybatis.builder;

import com.tk.mybatis.mapping.ParameterMapping;
import com.tk.mybatis.mapping.SqlSource;
import com.tk.mybatis.parsing.GenericTokenParser;
import com.tk.mybatis.parsing.TokenHandler;
import com.tk.mybatis.reflection.MetaObject;
import com.tk.mybatis.session.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/8/29 19:53
 * @Version 1.0
 * @Description sql构建器
 */
public class SqlSourceBuilder extends BaseBuilder{
    private static final String parameterProperties = "javaType,jdbcType,mode,numericScale,resultMap,typeHandler,jdbcTypeName";

    public SqlSourceBuilder(Configuration configuration) {
        super(configuration);
    }


    public SqlSource parser(String originalSql, Class<?> parameterType, HashMap<String, Object> additionalParameters) {
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration,parameterType,additionalParameters);
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        String sql = parser.parse(originalSql);

        //返回静态SQL
        return new StaticSqlSource(configuration,sql,handler.getParameterMappings());
    }

    private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {

        private List<ParameterMapping> parameterMappings = new ArrayList<>();
        private Class<?> parameterType;
        private MetaObject metaParameters;

        public ParameterMappingTokenHandler(Configuration configuration, Class<?> parameterType, Map<String,Object> additionalParameters){
            super(configuration);
            this.parameterType = parameterType;
            this.metaParameters = configuration.newMetaObject(additionalParameters);
        }

        public List<ParameterMapping> getParameterMappings(){return parameterMappings;}

        @Override
        public String handlerToken(String content) {
            parameterMappings.add(buildParameterMapping(content));
            return "?";
        }

        /**
         * 构建参数映射
         * @param content
         * @return
         */
        private ParameterMapping buildParameterMapping(String content){
            //先解析参数映射，就是转化为一个HashMap ｜ #{favouriteSection,jdbcType = VARCHAR}
            Map<String, String> propertiesMap = new ParmeterExpression(content);
            String property = propertiesMap.get("property");
            Class<?> parameterType = this.parameterType;
            ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration,property,parameterType);
            return builder.build();
        }
    }
}
