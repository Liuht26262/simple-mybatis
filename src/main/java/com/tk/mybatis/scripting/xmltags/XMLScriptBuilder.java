package com.tk.mybatis.scripting.xmltags;

import com.tk.mybatis.builder.BaseBuilder;
import com.tk.mybatis.mapping.SqlSource;
import com.tk.mybatis.scripting.defaults.RawSqlSource;
import com.tk.mybatis.session.Configuration;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/29 22:08
 * @Version 1.0
 * @Description XML脚本构建器
 */
public class XMLScriptBuilder extends BaseBuilder {
    private Element element;
    private boolean isDynamic;
    private Class<?> parameterType;

    public XMLScriptBuilder(Configuration configuration, Element script, Class<?> parameterTypeClass) {
        super(configuration);
        this.element = script;
        this.parameterType = parameterTypeClass;
    }

    public SqlSource parseScriptNode(){
        List<SqlNode> contents = parseDynamicTags(element);
        MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
        return new RawSqlSource(configuration,mixedSqlNode,parameterType);
    }

    /**
     *
     * @param element
     * @return
     */
    private List<SqlNode> parseDynamicTags(Element element) {
        ArrayList<SqlNode> contents = new ArrayList<>();
        //element.getText 拿到SQL
        String data = element.getText();
        contents.add(new StaticTextSqlNode(data));
        return contents;

    }
}
