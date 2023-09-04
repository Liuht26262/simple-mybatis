package com.tk.mybatis.scripting.xmltags;

/**
 * @Author liuht
 * @Date 2023/8/29 22:14
 * @Version 1.0
 * @Description 静态文本SQL节点
 */
public class StaticTextSqlNode implements SqlNode {
    private String text;

    public StaticTextSqlNode(String text) {
        this.text = text;
    }


    @Override
    public boolean apply(DynamicContext context) {
        //将文本加入context
        context.appendSql(text);
        return true;
    }
}
