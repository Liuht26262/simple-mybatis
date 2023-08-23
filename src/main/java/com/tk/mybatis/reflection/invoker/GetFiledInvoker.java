package com.tk.mybatis.reflection.invoker;

import java.lang.reflect.Field;

/**
 * @Author liuht
 * @Date 2023/8/14 19:01
 * @Version 1.0
 * @Description get方法反射调用类
 */
public class GetFiledInvoker implements Invoker{
    private Field field;

    public GetFiledInvoker(Field field) {
        this.field = field;
    }


    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        return field.get(target);
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
