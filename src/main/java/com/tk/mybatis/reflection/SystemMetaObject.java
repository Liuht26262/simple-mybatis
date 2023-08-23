package com.tk.mybatis.reflection;

import com.tk.mybatis.reflection.factory.DefaultObjectFactory;
import com.tk.mybatis.reflection.factory.ObjectFactory;
import com.tk.mybatis.reflection.wrapper.DefaultObjectWrapperFactory;
import com.tk.mybatis.reflection.wrapper.ObjectWrapperFactory;

/**
 * @Author liuht
 * @Date 2023/8/16 21:41
 * @Version 1.0
 * @Description 一些系统级别的元对象
 */
public class SystemMetaObject {
    public static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    public static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    public static final MetaObject NULL_META_OBJECT = MetaObject.forObject(NullObject.class,DEFAULT_OBJECT_FACTORY
            ,DEFAULT_OBJECT_WRAPPER_FACTORY);


    private static class NullObject{

    }

    public static MetaObject forObject(Object object) {
        return MetaObject.forObject(object, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY);
    }
}
