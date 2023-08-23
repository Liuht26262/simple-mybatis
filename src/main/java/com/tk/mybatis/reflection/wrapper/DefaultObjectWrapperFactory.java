package com.tk.mybatis.reflection.wrapper;

import com.tk.mybatis.reflection.MetaObject;

/**
 * @Author liuht
 * @Date 2023/8/16 21:14
 * @Version 1.0
 * @Description 默认对象工厂
 */
public class DefaultObjectWrapperFactory implements ObjectWrapperFactory{
    @Override
    public boolean hasWrapperFor(Object object) {
        return false;
    }

    @Override
    public ObjectWrapper getWrapper(MetaObject metaObject, Object object) {
        throw new RuntimeException("The DefaultObjectWrapperFactory should never be called to provide an ObjectWrapper.");
    }
}
