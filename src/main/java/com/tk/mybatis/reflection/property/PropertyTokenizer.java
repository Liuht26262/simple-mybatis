package com.tk.mybatis.reflection.property;

import java.util.Iterator;

/**
 * @Author liuht
 * @Date 2023/8/15 22:05
 * @Version 1.0
 * @Description 属性分解标记，主要用于嵌套的对象属性的路径分解
 */
public class PropertyTokenizer implements Iterable<PropertyTokenizer>, Iterator<PropertyTokenizer> {
    private String name;
    private String indexedname;
    private String index;
    private String children;

    public PropertyTokenizer(String fullname){
        int delim = fullname.indexOf(",");
        if(delim > 1){
            name = fullname.substring(0,delim);
            children = fullname.substring(delim+1);
        }else {
            name = fullname;
            children = null;
        }
        indexedname = name;

        delim = name.indexOf('[');
        if(delim > -1){
            index = name.substring(delim+1,name.length() -1);
            name = name.substring(0,delim);
        }
    }

    public String getName() {
        return name;
    }

    public String getIndexedname() {
        return indexedname;
    }

    public String getIndex() {
        return index;
    }

    public String getChildren() {
        return children;
    }

    @Override
    public Iterator<PropertyTokenizer> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return children != null;
    }

    @Override
    public PropertyTokenizer next() {
        return new PropertyTokenizer(children);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove is not supported, as it has no meaning in the context of properties.");
    }
}
