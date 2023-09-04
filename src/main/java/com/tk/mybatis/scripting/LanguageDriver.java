package com.tk.mybatis.scripting;

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
    SqlSource createSqlSource(Configuration configuration, Element element, Class<?> parameterTypeClass) ;
}
