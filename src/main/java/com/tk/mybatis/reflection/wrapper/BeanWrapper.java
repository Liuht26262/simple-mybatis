package com.tk.mybatis.reflection.wrapper;

import com.tk.mybatis.reflection.MetaClass;
import com.tk.mybatis.reflection.MetaObject;
import com.tk.mybatis.reflection.SystemMetaObject;
import com.tk.mybatis.reflection.factory.ObjectFactory;
import com.tk.mybatis.reflection.invoker.Invoker;
import com.tk.mybatis.reflection.property.PropertyTokenizer;

import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/17 21:43
 * @Version 1.0
 * @Description 描述
 */
public class BeanWrapper extends BaseWrapper {
    //原来的对象
    private Object object;
    //元类
    private MetaClass metaClass;

    public BeanWrapper(MetaObject metaObject,Object object) {
        super(metaObject);
        this.object = object;
        this.metaClass = MetaClass.forClass(object.getClass());
    }

    /**
     * 获取属性值
     * @param prop
     * @return
     */
    @Override
    public Object get(PropertyTokenizer prop) {
        //如果有index(有中括号)，说明是集合，那就要解析集合，调用的是BaseWrapper.resolveCollection 和 getCollectionValue
        if(prop.getIndex() != null){
            Object collection = resolveCollection(prop, object);
            return getCollectionValue(prop,collection);
        }else{
            //否则，就getBeanProperty
            return getBeanProperty(prop,object);
        }
    }

    private Object getBeanProperty(PropertyTokenizer prop, Object object) {
        try{
            Invoker method = metaClass.getGetInvoker(prop.getName());
            return method.invoke(object,NO_ARGUMENTS);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException("Could not get property '" + prop.getName() + "' from " +
                    object.getClass() + ".  Cause: " + t.toString(), t);
        }
    }

    @Override
    public void set(PropertyTokenizer prop, Object value) {
        //如果有index(有中括号)，说明是集合，那就要解析集合，调用的是BaseWrapper.resolveCollection 和 getCollectionValue
        if(prop.getIndex() != null){
            Object collection = resolveCollection(prop, object);
            setCollectionValue(prop,collection,value);
        }else {
            setBeanProperty(prop,object,value);
        }
    }

    private void setBeanProperty(PropertyTokenizer prop, Object object, Object value) {
        try {
            //获取set方法，然后直接调用
            Invoker method = metaClass.getSetInvoker(prop.getName());
            Object[] param = {value};
            method.invoke(object,param);
        } catch (Exception t) {
            throw new RuntimeException("Could not set property '" + prop.getName() + "' of '" + object.getClass()
                    + "' with value '" + value + "' Cause: " + t.toString(), t);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return metaClass.findProperty(name, useCamelCaseMapping);
    }

    @Override
    public String[] getGetterNames() {
        return metaClass.getGetterNames();
    }

    @Override
    public String[] getSetterNames() {
        return metaClass.getSetterNames();
    }

    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if(prop.hasNext()){
            MetaObject metaValue = this.metaObject.metaObjectForProperty(prop.getIndexedname());
            if(metaValue == SystemMetaObject.NULL_META_OBJECT){
                return metaClass.getGetterType(name);
            }else {
                return metaValue.getGetterType(prop.getChildren());
            }
        }else {
            return metaClass.getGetterType(name);
        }
    }

    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if(prop.hasNext()){
            MetaObject metaValue = this.metaObject.metaObjectForProperty(prop.getIndexedname());
            if(metaValue == SystemMetaObject.NULL_META_OBJECT){
                return metaClass.getSetterType(name);
            }else {
                return metaValue.getSetterType(prop.getChildren());
            }
        }else {
            return metaClass.getSetterType(name);
        }
    }

    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (metaClass.hasGetter(prop.getIndexedname())) {
                MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedname());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return metaClass.hasGetter(name);
                } else {
                    return metaValue.hasGetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return metaClass.hasGetter(name);
        }
    }

    @Override
    public boolean hasSetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if (prop.hasNext()) {
            if (metaClass.hasSetter(prop.getIndexedname())) {
                MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedname());
                if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
                    return metaClass.hasSetter(name);
                } else {
                    return metaValue.hasSetter(prop.getChildren());
                }
            } else {
                return false;
            }
        } else {
            return metaClass.hasSetter(name);
        }
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory) {
        MetaObject metaValue;
        Class<?> type = getSetterType(name);
        try{
            Object newObject = objectFactory.create(type);
            metaValue = metaObject.forObject(newObject, metaObject.getObjectFactory(),
                    metaObject.getObjectWrapperFactory());
            set(prop,newObject);
        }catch (Exception e){
            throw new RuntimeException("Cannot set value of property '" + name + "' because '" + name +
                    "' is null and cannot be instantiated on instance of " + type.getName() + ". Cause:" + e.toString(), e);
        }
        return metaValue;
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public void add(Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void addAll(List<E> element) {
        throw new UnsupportedOperationException();
    }
}
