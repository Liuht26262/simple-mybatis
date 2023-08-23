package com.tk.mybatis.reflection.factory;

import javafx.beans.property.Property;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @Author liuht
 * @Date 2023/8/16 16:29
 * @Version 1.0
 * @Description 描述
 */
public class DefaultObjectFactory implements ObjectFactory{
    @Override
    public void setProperty(Property property) {

    }

    @Override
    public <T> T create(Class<?> clazz) {
        return create(clazz,null,null);
    }

    @Override
    public <T> T create(Class<?> clazz, List<Class<?>> constructorTypes, List<Object> constructorArgs) {
        Class classToCreate = resolveInterfacte(clazz);
        //类实例化
        return (T)instantiateClass(classToCreate,constructorTypes,constructorArgs);
    }

    /**
     * 类实例化
     * @param classToCreate
     * @param constructorTypes
     * @param constructorArgs
     * @param <T>
     * @return
     */
    private <T> T instantiateClass(Class<T> classToCreate, List<Class<?>> constructorTypes,
                                   List<java.lang.Object> constructorArgs) {
        try{
            Constructor<T> constructor;
            //如果没有构造函数类型以及请求参数，就调用无参构造函数
            if(constructorTypes == null && constructorArgs == null){
                constructor = classToCreate.getDeclaredConstructor();
                if(!constructor.isAccessible()){
                    constructor.setAccessible(true);
                }
                
                return constructor.newInstance();
            }
            //调用有参构造函数
            constructor = classToCreate.getDeclaredConstructor(constructorTypes
                    .toArray(new Class[constructorTypes.size()]));
            if(!constructor.isAccessible()){
                constructor.setAccessible(true);
            }
            return constructor.newInstance(constructorArgs.toArray(new Object[constructorArgs.size()]));
        }catch (Exception e){
            //创建报错，先包装一下进行返回
            StringBuilder argsType = new StringBuilder();
            if(constructorTypes!=null){
                for(Class clazz : constructorTypes){
                    argsType.append(clazz.getSimpleName());
                    argsType.append(",");
                }
            }

            StringBuilder argValues = new StringBuilder();
            if(constructorArgs!=null){
                for(Object obj : constructorArgs){
                    argValues.append(obj);
                    argValues.append(",");
                }
            }

            throw new RuntimeException("Error instantiating " + classToCreate + " with invalid types (" + argsType + ") " +
                    "or values (" + argValues + "). Cause: " + e, e);
        }
    }


    /**
     * 解析接口
     * @param clazz
     * @return
     */
    private Class<?> resolveInterfacte(Class<?> clazz) {
        Class<?> classToCreate;
        if(clazz == List.class || clazz == Collection.class || clazz == Iterable.class){
            classToCreate = ArrayList.class;
        }else if(clazz == Map.class){
            classToCreate = HashMap.class;
        }else if(clazz == Set.class){
            classToCreate = HashSet.class;
        }else {
            classToCreate = clazz;
        }
        
        return classToCreate;
    }

    @Override
    public <T> boolean isCollection(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }
}
