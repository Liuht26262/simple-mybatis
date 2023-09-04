package com.tk.mybatis.scripting.xmltags;

/**
 * @Author liuht
 * @Date 2023/8/28 20:56
 * @Version 1.0
 * @Description 描述
 */
public interface SqlNode {
    boolean apply(DynamicContext context);
}
