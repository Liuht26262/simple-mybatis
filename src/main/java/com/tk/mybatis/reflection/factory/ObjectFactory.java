package com.tk.mybatis.reflection.factory;

import javafx.beans.property.Property;

import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/16 15:29
 * @Version 1.0
 * @Description 描述
 */
public interface ObjectFactory {
    /**
     * 属性赋值
     * @param property
     */
    void setProperty(Property property);

    /**
     * 生产对象
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T create(Class<?> clazz);

    /**
     * 根据指定的构造函数生产对象
     * @param clazz
     * @param constructorTypes
     * @param constractorArgs
     * @param <T>
     * @return
     */
    <T> T create(Class<?> clazz, List<Class<?>> constructorTypes, List<Object> constractorArgs);

    /**
     * 判断一个对象是否是集合
     * @param type
     * @param <T>
     * @return
     */
    <T> boolean isCollection(Class<?> type);
 }
