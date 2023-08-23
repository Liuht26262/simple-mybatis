package com.tk.mybatis.reflection;

import com.tk.mybatis.reflection.invoker.GetFiledInvoker;
import com.tk.mybatis.reflection.invoker.Invoker;
import com.tk.mybatis.reflection.invoker.MethodInvoker;
import com.tk.mybatis.reflection.property.PropertyTokenizer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * @Author liuht
 * @Date 2023/8/15 21:31
 * @Version 1.0
 * @Description 元类 相当于反射器的包装类
 */
public class MetaClass {
    private Reflector reflector;

    public MetaClass(Class<?> type){
        this.reflector = Reflector.forClass(type);
    }

    public static MetaClass forClass(Class<?> clazz){
        return new MetaClass(clazz);
    }

    public static boolean isClassCacheEnable(){
        return Reflector.isClassCacheEnable();
    }

    public static void setClassCacheEnable(boolean classCacheEnable){
        Reflector.setClassCacheEnbale(classCacheEnable);
    }

    public MetaClass metaClassForProperty(String name){
        Class<?> propType = reflector.getGetterType(name);
        return MetaClass.forClass(propType);
    }

    public String findProperty(String name){
        StringBuilder prop = buildProperty(name,new StringBuilder());
        return prop.length() > 0 ? prop.toString() : null;
    }

    public String findProperty(String name,boolean useCamelCaseMapping){
        if (useCamelCaseMapping){
            name = name.replace("_","");
        }
        return findProperty(name);
    }

    /**
     * 构建嵌套对象的访问路径
     * @param name
     * @param builder
     * @return
     */
    private StringBuilder buildProperty(String name, StringBuilder builder) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if(prop.hasNext()){
            String propertyName = reflector.findPropertyname(prop.getName());
            if(propertyName != null){
                builder.append(propertyName);
                builder.append(".");
                MetaClass metaClass = metaClassForProperty(propertyName);
                metaClass.buildProperty(prop.getChildren(),builder);
            }else {
                String propertyname = reflector.findPropertyname(name);
                if(propertyname != null){
                    builder.append(propertyName);
                }
            }
        }
        return builder;
    }

    /**
     * 查看是否拥有setter方法
     * @param name
     * @return
     */
    public boolean hasSetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if(prop.hasNext()){
            if(reflector.hasSetter(prop.getName())){
                // 这里是递归调用，确保所有的属性方法都有set方法
                MetaClass metaClass = metaClassForProperty(prop.getName());
                return metaClass.hasSetter(prop.getChildren());
            }else{
                return false;
            }
        }else{
            return reflector.hasSetter(prop.getName());
        }
    }


    /**
     * 获取set参数类型
     * @param name
     * @return
     */
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if(prop.hasNext()){
            MetaClass metaClass = metaClassForProperty(prop.getName());
            return metaClass.getSetterType(prop.getChildren());
        }else{
            return reflector.getSetterType(prop.getName());
        }
    }

    public boolean hasGetter(String name){
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if(prop.hasNext()){
            if(reflector.hasGetter(prop.getName())){
                MetaClass metaClass = metaClassForProperty(prop);
                return metaClass.hasGetter(prop.getChildren());
            }else{
                return false;
            }
        } else {
            return reflector.hasGetter(prop.getName());
        }
    }

    /**
     * 获取get参数类型
     * @param name
     * @return
     */
    public Class<?> getGetterType(String name){
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if(prop.hasNext()){
            MetaClass metaClass = metaClassForProperty(prop.getName());
            return metaClass.getSetterType(name);
        } else {
            return  reflector.getGetterType(prop.getName());
        }
    }

    /**
     * 获取给定属性的元类
     * @param prop
     * @return
     */
    private MetaClass metaClassForProperty(PropertyTokenizer prop){
        Class<?> propType = getGetterType(prop);
        return MetaClass.forClass(propType);
    }

    private Class<?> getGetterType(PropertyTokenizer prop) {
        Class<?> type = reflector.getGetterType(prop.getName());
        if(prop.getIndex() != null && Collection.class.isAssignableFrom(type)){
            Type returnType = getGenericGetterType(prop.getName());
            //检查泛型返回类型是否为参数化类型，即是否包含泛型信息
            if(returnType instanceof ParameterizedType){
                //如果事泛型，就获取实际的反省参数类型
                Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
                if(actualTypeArguments != null && actualTypeArguments.length == 1){
                    returnType = actualTypeArguments[0];
                    if(returnType instanceof  Class){
                        //如果是Class类型，就将泛型类型作为属性类型
                        type = (Class<?>)returnType;
                    } else if(returnType instanceof ParameterizedType){
                        //如果是参数化类型，就将其其原始类型作为属性类型
                        type = (Class<?>)((ParameterizedType)returnType).getRawType();
                    }
                }
            }
        }

        return type;
    }




    private Type getGenericGetterType(String name) {
        try{
            Invoker invoker = reflector.getGetInvoker(name);
            if(invoker instanceof MethodInvoker){
                Field _method = MethodInvoker.class.getDeclaredField("method");
                _method.setAccessible(true);
                Method method = (Method) _method.get(invoker);
                return method.getGenericReturnType();
            }else if(invoker instanceof GetFiledInvoker){
                Field _field = GetFiledInvoker.class.getDeclaredField("field");
                _field.setAccessible(true);
                Field field = (Field) _field.get(invoker);
                return field.getGenericType();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }


    public String[] getGetterNames(){
        return reflector.getGetablePropertyNames();
    }

    public String[] getSetterNames(){
        return reflector.getSetablePropertyNames();
    }

    public Invoker getGetInvoker(String name){
        return reflector.getGetInvoker(name);
    }

    public Invoker getSetInvoker(String name){
        return reflector.getSetInvoker(name);
    }


}
