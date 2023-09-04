package com.tk.mybatis.scripting.xmltags;

import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/29 22:16
 * @Version 1.0
 * @Description 混合sql节点
 */
public class MixedSqlNode implements SqlNode{
    //组合模式，拥有一个sqlNode的List
    private List<SqlNode> contents;


    public MixedSqlNode(List<SqlNode> contents) {
        this.contents = contents;
    }


    @Override
    public boolean apply(DynamicContext context) {
        //依次调用list里每个元素的apply
        contents.forEach(node -> node.apply(context));
        return true;
    }
}
