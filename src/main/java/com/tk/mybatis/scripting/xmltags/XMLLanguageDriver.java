package com.tk.mybatis.scripting.xmltags;

import com.tk.mybatis.executor.parameter.ParameterHandler;
import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.mapping.SqlSource;
import com.tk.mybatis.scripting.LanguageDriver;
import com.tk.mybatis.scripting.defaults.DefaultParameterHandler;
import com.tk.mybatis.session.Configuration;
import org.dom4j.Element;

/**
 * @Author liuht
 * @Date 2023/8/29 22:05
 * @Version 1.0
 * @Description XML语言驱动器
 */
public class XMLLanguageDriver implements LanguageDriver {
    @Override
    public SqlSource createSqlSource(Configuration configuration, Element script, Class<?> parameterTypeClass) {
        //用XMl脚本构建器解析
        XMLScriptBuilder xmlScriptBuilder = new XMLScriptBuilder(configuration,script,parameterTypeClass);
        return xmlScriptBuilder.parseScriptNode();
    }

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameter, BoundSql boundSql) {
        return new DefaultParameterHandler(mappedStatement,parameter,boundSql);
    }
}
