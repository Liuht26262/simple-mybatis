package com.tk.mybatis.reflection.wrapper;

import com.tk.mybatis.reflection.MetaObject;
import com.tk.mybatis.reflection.factory.ObjectFactory;
import com.tk.mybatis.reflection.property.PropertyTokenizer;

import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/16 14:40
 * @Version 1.0
 * @Description 对象包装器，包装对象的所有操作方法
 */
public interface ObjectWrapper {

    //get方法
    Object get(PropertyTokenizer prop);

    //set方法
    void set(PropertyTokenizer prop,Object value);

    //获取属性的访问路径
    String findProperty(String name,boolean useCamelCaseMapping);

    //获取Getter的名字列表
    String[] getGetterNames();

    //获取Setter的名字列表
    String[] getSetterNames();

    //获取get方法的返回类型
    Class<?> getGetterType(String name);

    //获取set方法的参数类型
    Class<?> getSetterType(String name);

    //是否有指定的Getter
    boolean hasGetter(String name);

    //是否有指定的Setter
    boolean hasSetter(String name);

    //初始化元对象
    MetaObject instantiatePropertyValue(String name, PropertyTokenizer propertyTokenizer, ObjectFactory objectFactory);

    //判断是否是集合
    boolean isCollection();

    //添加属性
    void add(Object element);

    //添加属性
    <E> void addAll(List<E> element);
}
