package com.tk.mybatis.builder.xml;

import com.tk.mybatis.builder.BaseBuilder;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.mapping.SqlCommandType;
import com.tk.mybatis.mapping.SqlSource;
import com.tk.mybatis.scripting.LanguageDriver;
import com.tk.mybatis.scripting.LanguageDriverRegistry;
import com.tk.mybatis.session.Configuration;
import org.dom4j.Element;

import java.util.Locale;

/**
 * @Author liuht
 * @Date 2023/8/23 22:18
 * @Version 1.0
 * @Description 描述
 */
public class XMLStatementBuilder extends BaseBuilder {
    private String currentNamespace;
    private Element element;

    public XMLStatementBuilder(Configuration configuration, Element element, String currentNamespace) {
        super(configuration);
        this.element = element;
        this.currentNamespace = currentNamespace;
    }

    //解析语句(select|insert|update|delete)
    //<select
    //  id="selectPerson"
    //  parameterType="int"
    //  parameterMap="deprecated"
    //  resultType="hashmap"
    //  resultMap="personResultMap"
    //  flushCache="false"
    //  useCache="true"
    //  timeout="10000"
    //  fetchSize="256"
    //  statementType="PREPARED"
    //  resultSetType="FORWARD_ONLY">
    //  SELECT * FROM PERSON WHERE ID = #{id}
    //</select>
    public void parseStatementNode(){
        //绑定的方法名
        String id = element.attributeValue("id");
        //参数类型
        String parametertype = element.attributeValue("parameterType");
        Class<?> parameterTypeClass = resolveAlias(parametertype);
        //返回类型
        String resultType = element.attributeValue("resultType");
        Class<?> resultTypeClass = resolveAlias(resultType);

        //获取命令类型（select|update|delete|insert）
        String nodeName = element.getName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));

        //获取默认语言驱动器
        Class<?> langClass = configuration.getLanguageRegistry().getDefaultDriverClass();
        LanguageDriver driver = configuration.getLanguageRegistry().getDriver(langClass);

        SqlSource sqlSource = driver.createSqlSource(configuration, element, parameterTypeClass);

        MappedStatement mappedStatement = new MappedStatement.Builder(configuration, currentNamespace + "."
                + id, sqlCommandType, sqlSource, resultTypeClass).build();

        //添加解析SQL
        configuration.addMappedstatement(mappedStatement);
    }

}
