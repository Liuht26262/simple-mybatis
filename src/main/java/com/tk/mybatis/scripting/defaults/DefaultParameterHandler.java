package com.tk.mybatis.scripting.defaults;

import com.alibaba.fastjson.JSON;
import com.tk.mybatis.executor.parameter.ParameterHandler;
import com.tk.mybatis.executor.resultset.DefaultResultSetHandler;
import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.mapping.ParameterMapping;
import com.tk.mybatis.reflection.MetaObject;
import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.type.JdbcType;
import com.tk.mybatis.type.TypeHandler;
import com.tk.mybatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * @Author liuht
 * @Date 2023/9/4 22:38
 * @Version 1.0
 * @Description 默认参数处理器
 */
public class DefaultParameterHandler implements ParameterHandler {

    private Logger log = LoggerFactory.getLogger(DefaultParameterHandler.class);

    private final TypeHandlerRegistry typeHandlerRegistry;

    private final MappedStatement mappedStatement;
    private final Object parameterObject;
    private BoundSql boundSql;
    private Configuration configuration;

    public DefaultParameterHandler(MappedStatement mappedStatement,Object parameterObject,BoundSql boundSql){
        this.mappedStatement = mappedStatement;
        this.configuration = mappedStatement.getConfiguration();
        this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }



    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    @Override
    public void setParameters(PreparedStatement ps) throws SQLException {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        if(null != parameterMappings){
            for(int i = 0;i<parameterMappings.size();i++){
                ParameterMapping parameterMapping = parameterMappings.get(i);
                String propertyName = parameterMapping.getProperty();
                Object value;
                if(typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())){
                    value = parameterObject;
                }else{
                    // 通过MetaObject.getValue 反射取得值设置进去
                    MetaObject metaObject = configuration.newMetaObject(parameterObject);
                    value = metaObject.getValue(propertyName);
                }
                JdbcType jdbcType = parameterMapping.getJdbcType();

                //设置参数
                log.info("根据每个ParameterMapping中的TypeHandler设置对应的参数信息 value:{}", JSON.toJSONString(value));
                TypeHandler typeHandler = parameterMapping.getTypeHandler();
                typeHandler.setParameter(ps,i+1,value,jdbcType);
            }
        }
    }
}
