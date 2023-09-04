package com.tk.mybatis.builder.xml;

import com.tk.mybatis.builder.BaseBuilder;
import com.tk.mybatis.io.Resources;
import com.tk.mybatis.session.Configuration;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/23 21:51
 * @Version 1.0
 * @Description XML映射构造器
 */
public class XMLMapperBuilder extends BaseBuilder {
    private Element element;
    private String resource;
    private String currentNamespace;


    public XMLMapperBuilder(InputStream inpurStream, Configuration configuration, String resource) throws DocumentException {
        this(new SAXReader().read(inpurStream),configuration,resource);
    }

    public XMLMapperBuilder(Document document, Configuration configuration, String resource) {
        super(configuration);
        this.element = document.getRootElement();
        this.resource = resource;
    }

    /**
     * 解析
     * @throws Exception
     */
    public void parse() throws Exception{
        //如果当前资源没有加载过才会进行加载
        if(!configuration.isResourceLoaded(resource)){
            configurationElement(element);
            //标记一下，表示加载过了
            configuration.addLoadedResources(resource);
            //绑定映射器到namespace
            configuration.addMapper(Resources.getClass(currentNamespace));
        }
    }

    private void configurationElement(Element element) {
        //配置namespace
        currentNamespace = element.attributeValue("namespace");
        if("".equals(currentNamespace)){
            throw new RuntimeException("Mapper's namespace cannot be empty");
        }

        //配置select|update|insert|delete
        buildStatementFromContext(element.elements("select"));
    }


    private void buildStatementFromContext(List<Element> list) {
        for(Element element : list){
            final XMLStatementBuilder statementBuilder = new XMLStatementBuilder(configuration,element,currentNamespace);
            statementBuilder.parseStatementNode();
        }
    }

}
