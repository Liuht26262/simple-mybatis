package com.tk.mybatis.scripting;

import com.tk.mybatis.executor.parameter.ParameterHandler;
import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.mapping.SqlSource;
import com.tk.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * @Author liuht
 * @Date 2023/8/23 22:36
 * @Version 1.0
 * @Description 描述
 */
public interface LanguageDriver {
    /**
     * 创建SQL源码
     * @param configuration
     * @param element
     * @param parameterTypeClass
     * @return
     */
    SqlSource createSqlSource(Configuration configuration, Element element, Class<?> parameterTypeClass) ;

    /**
     * 创建参数处理器
     * @param mappedStatement
     * @param parameter
     * @param boundSql
     * @return
     */
    ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameter, BoundSql boundSql);
}
