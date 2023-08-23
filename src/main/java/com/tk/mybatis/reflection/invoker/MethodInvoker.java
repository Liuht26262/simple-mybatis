package com.tk.mybatis.reflection.invoker;

import java.lang.reflect.Method;

/**
 * @Author liuht
 * @Date 2023/8/14 18:58
 * @Version 1.0
 * @Description 方法调用
 */
public class MethodInvoker implements Invoker{
    private Class<?> type;
    private Method method;

    public MethodInvoker(Method method){
        this.method = method;
        //如果只有一个参数，就返回parameterType,否则返回resultType
        Class<?>[] parameterTypes = method.getParameterTypes();
        if(parameterTypes.length == 1){
            this.type = parameterTypes[0];
        }else {
            this.type = method.getReturnType();
        }
    }

    @Override
    public Object invoke(Object target, Object[] args) throws Exception {
        return method.invoke(target,args);
    }

    @Override
    public Class<?> getType() {
        return type;
    }
}
