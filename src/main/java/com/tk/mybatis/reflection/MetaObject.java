package com.tk.mybatis.reflection;

import com.tk.mybatis.reflection.factory.ObjectFactory;
import com.tk.mybatis.reflection.property.PropertyTokenizer;
import com.tk.mybatis.reflection.wrapper.*;

import java.util.Collection;
import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/8/16 14:37
 * @Version 1.0
 * @Description 元对象
 */
public class MetaObject {
    //元对象
    private Object originalObject;
    //对象包装器
    private ObjectWrapper objectWrapper;
    //对象工厂
    private ObjectFactory objectFactory;
    //对象包装工厂
    private ObjectWrapperFactory objectWrapperFactory;

    public MetaObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory) {
        this.originalObject = object;
        this.objectFactory = objectFactory;
        this.objectWrapperFactory = objectWrapperFactory;

        if(object instanceof ObjectWrapper){
            //如果对象本身已经是ObjectWrapper类型，就直接赋值给ojectWrapper
            this.objectWrapper = (ObjectWrapper)object;
        }else if(objectWrapperFactory.hasWrapperFor(object)){
            //如果有包装器，就调用ObjectWrapperFactory.getWrapperFor
            this.objectWrapper = objectWrapperFactory.getWrapper(this,object);
        }else if(object instanceof Map){
            //如果是Map类型，就返回MapWrapper
            this.objectWrapper = new MapWrapper(this,(Map)object);
        }else if(object instanceof Collection){
            //如果是Collection类型，就返回CollectionWrapper
            this.objectWrapper = new CollectionWrapper(this,(Collection)object);
        }else{
            //否则返回BeanWrapper
            this.objectWrapper = new BeanWrapper(this,object);
        }
    }

    /**
     * 获取值
     * @param name
     * @return
     */
    public Object getValue(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if(prop.hasNext()){
            MetaObject metaObject = metaObjectForProperty(prop.getIndexedname());
            if(metaObject == SystemMetaObject.NULL_META_OBJECT){
                //如果上层就是null了，那就结束，返回null
                return null;
            }else{
                //否则继续看下一层，递归调用getValue
                return metaObject.getValue(prop.getChildren());
            }
        }else{
            return objectWrapper.get(prop);
        }

    }

    /**
     * 设置属性值
     * @param name
     * @param value
     */
    public void setValue(String name,Object value){
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if(prop.hasNext()){
            MetaObject metaObject = metaObjectForProperty(prop.getIndexedname());
            if(metaObject == SystemMetaObject.NULL_META_OBJECT){
                //如果上层就是null，就往下遍历
                return;
            }else{
                metaObject = objectWrapper.instantiatePropertyValue(name,prop,objectFactory);
            }

            //遍历它的嵌套对象
            metaObject.setValue(prop.getChildren(),value);
        }else {
            //如果只有一层，那就直接委派给ojectWrapper.set
            objectWrapper.set(prop,value);
        }
    }

    /**
     * 为属性生成元对象
     * @param indexedname
     * @return
     */
    public MetaObject metaObjectForProperty(String indexedname) {
        //递归调用
        Object value = getValue(indexedname);
        return MetaObject.forObject(value,objectFactory,objectWrapperFactory);
    }

    public static MetaObject forObject(Object value, ObjectFactory objectFactory,
                                        ObjectWrapperFactory objectWrapperFactory) {
        if(value == null){
            return SystemMetaObject.NULL_META_OBJECT;
        }else {
            return new MetaObject(value,objectFactory,objectWrapperFactory);
        }
    }

    public Class<?> getSetterType(String name) {
        return objectWrapper.getSetterType(name);
    }

    public Class<?> getGetterType(String name) {
        return objectWrapper.getGetterType(name);
    }

    public boolean hasGetter(String name) {
        return objectWrapper.hasGetter(name);
    }

    public boolean hasSetter(String name) {
        return objectWrapper.hasSetter(name);
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public ObjectWrapperFactory getObjectWrapperFactory() {
        return objectWrapperFactory;
    }

    public String [] getGetterNames(){
        return objectWrapper.getGetterNames();
    }

    public String[] getSetterNames(){
        return objectWrapper.getSetterNames();
    }

    public Object getOriginalObject() {
        return originalObject;
    }

    public ObjectWrapper getObjectWrapper() {
        return objectWrapper;
    }
}
