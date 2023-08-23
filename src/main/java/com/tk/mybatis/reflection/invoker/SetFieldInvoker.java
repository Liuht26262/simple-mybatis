package com.tk.mybatis.reflection.invoker;

import java.lang.reflect.Field;

/**
 * @Author liuht
 * @Date 2023/8/14 19:03
 * @Version 1.0
 * @Description set方法反射调用类
 */
public class SetFieldInvoker implements Invoker{
    private Field field;

    public SetFieldInvoker(Field field){
        this.field = field;
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        field.set(target,args[0]);
        return null;
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }
}
