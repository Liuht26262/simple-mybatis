package com.tk.mybatis.reflection.wrapper;

import com.tk.mybatis.reflection.MetaObject;

/**
 * @Author liuht
 * @Date 2023/8/16 21:12
 * @Version 1.0
 * @Description 对象包装工厂
 */
public interface ObjectWrapperFactory {
    /**
     * 判断有没有包装器
     * @param object
     * @return
     */
    boolean hasWrapperFor(Object object);

    /**
     * 获取包装器
     * @param metaObject
     * @param object
     * @return
     */
    ObjectWrapper getWrapper(MetaObject metaObject,Object object);
}
