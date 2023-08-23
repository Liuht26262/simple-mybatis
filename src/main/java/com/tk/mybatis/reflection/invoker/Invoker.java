package com.tk.mybatis.reflection.invoker;

/**
 * @Author liuht
 * @Date 2023/8/14 18:56
 * @Version 1.0
 * @Description 反射调用接口
 */
public interface Invoker {
    Object invoke(Object target,Object[] args) throws Exception;

    Class<?> getType();
}
