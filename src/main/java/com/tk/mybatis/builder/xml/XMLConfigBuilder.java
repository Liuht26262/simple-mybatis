package com.tk.mybatis.builder.xml;

import com.tk.mybatis.builder.BaseBuilder;
import com.tk.mybatis.datasource.DataSourceFactory;
import com.tk.mybatis.io.Resources;
import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.Environment;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.mapping.SqlCommandType;
import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.transaction.TransactionFactory;
import com.tk.mybatis.type.TypeAliasRegistry;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;


import javax.sql.DataSource;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author liuht
 * @Date 2023/7/31 16:46
 * @Version 1.0
 * @Description XML解析器
 */
public class XMLConfigBuilder extends BaseBuilder {
    private static final String COMPILE = "(#\\{(.*?)})";
    private Element root;

    public XMLConfigBuilder(Reader reader) {
        //初始化配置类
        super(new Configuration());
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(reader);
            root = document.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @return
     */
    public Configuration parser() {
        try{
            mapperElement(root.element("mappers"));
            environmentElement(root.element("environments"));
        }catch (Exception e){
            throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
        return configuration;
    }

    /**
     * 解析environment标签
     * @param context
     */
    private void environmentElement(Element context) throws InstantiationException, IllegalAccessException {
        String environmentType = context.attributeValue("default");
        List<Element> environments = context.elements("environment");

        for(Element element : environments){
            String id = element.attributeValue("id");
            //这里主要查看配置的默认是哪个数据源
            if(environmentType.equals(id)){
                TransactionFactory transactionFactory = (TransactionFactory) typeAliasRegistry.resolveAlias(element
                        .element("transactionManager").attributeValue("type")).newInstance();
                Element dataSourceElement = element.element("dataSource");
                DataSourceFactory dataSourceFactory = (DataSourceFactory) typeAliasRegistry.resolveAlias(
                        dataSourceElement.attributeValue("type")).newInstance();
                List<Element> propertyList = dataSourceElement.elements("property");
                Properties properties = new Properties();
                for(Element property : propertyList){
                    properties.setProperty(property.attributeValue("name"),property.attributeValue("value"));
                }

                //创建数据源
                dataSourceFactory.setProperties(properties);
                DataSource dataSource = dataSourceFactory.getDataSource();

                //构建环境
                Environment environment = new Environment.Builder(id).transactionFactory(transactionFactory)
                        .datasource(dataSource).build();
                configuration.setEnvironment(environment);

            }
        }
    }

    /**
     * 解析mapper标签
     * @param mappers
     */
    private void mapperElement(Element mappers) throws IOException, DocumentException, ClassNotFoundException {
        List<Element> elements = mappers.elements("mapper");
        for(Element element : elements){
            String resource = element.attributeValue("resource");
            //这里获取的才是mapper.xml
            Reader resoucres = Resources.getResoucres(resource);
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(new InputSource(resoucres));
            Element rootElement = document.getRootElement();
            String nameSpace = rootElement.attributeValue("namespace");

            List<Element> elementList = rootElement.elements("select");
            for (Element e : elementList){
                String id = e.attributeValue("id");
                String parameterType = e.attributeValue("parameterType");
                String resultType = e.attributeValue("resultType");

                Map<Integer,String> parameterMap = new HashMap();
                String sql = e.getText();
                Pattern compile = Pattern.compile(COMPILE);
                Matcher matcher = compile.matcher(sql);
                for (int i = 0; matcher.find(); i++) {
                    //获取匹配项中的第一个匹配组 即完整的"#{}"格式
                    String g1 = matcher.group(1);
                    //获取匹配项中第二个匹配组 即不包含"#{}"的参数
                    String g2 = matcher.group(2);
                    parameterMap.put(i, g2);
                    //将所有的#{}替换成 ？ 以便后续使用预处理语句执行sql
                    sql = sql.replace(g1, "?");
                }
                String msId = nameSpace+"."+id;
                String name = e.getName();
                SqlCommandType sqlCommandType = SqlCommandType.valueOf(name.toUpperCase(Locale.ENGLISH));

                BoundSql boundSql = new BoundSql(sql, parameterType, resultType, parameterMap);
                MappedStatement mappedStatement = new MappedStatement.Builder(configuration, msId, sqlCommandType,boundSql)
                        .build();
                //添加解析后的sql语句
                configuration.addMappedstatement(mappedStatement);
            }

            //注册Mapper
            configuration.addMapper(Resources.getClass(nameSpace));

        }
    }
}
