package com.tk.mybatis.reflection.wrapper;

import com.tk.mybatis.reflection.MetaClass;
import com.tk.mybatis.reflection.MetaObject;
import com.tk.mybatis.reflection.SystemMetaObject;
import com.tk.mybatis.reflection.factory.ObjectFactory;
import com.tk.mybatis.reflection.property.PropertyTokenizer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/8/16 21:57
 * @Version 1.0
 * @Description 描述
 */
public class MapWrapper extends BaseWrapper {
    //原来的对象
    private Map<String,Object> map;
    //元类
    private MetaClass metaClass;

    public MapWrapper(MetaObject metaObject, Map map) {
        super(metaObject);
        this.map = map;
    }

    @Override
    public Object get(PropertyTokenizer prop) {
        //如果有index,说明是集合，那就要分解集合,调用的是BaseWrapper.resolveCollection 和 getCollectionValue
        if(prop.getIndex() != null){
            Object collection = resolveCollection(prop, map);
            return getCollectionValue(prop,collection);
        }else{
            return map.get(prop.getName());
        }
    }


    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if(prop.getIndex() != null){
            Object collection = resolveCollection(prop, value);
            setCollectionValue(prop,collection,value);
        }else {
            map.put(prop.getName(),value);
        }
    }

    @Override
    public String findProperty(String name, boolean useCamelCaseMapping) {
        return name;
    }

    @Override
    public String[] getGetterNames() {
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

    @Override
    public String[] getSetterNames() {
        return map.keySet().toArray(new String[map.keySet().size()]);
    }

    /**
     * 获取get方法参数类型
     * @param name
     * @return
     */
    @Override
    public Class<?> getGetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if(prop.hasNext()){
            MetaObject metaValue = metaObject.metaObjectForProperty(prop.getIndexedname());
            if(metaValue == SystemMetaObject.NULL_META_OBJECT){
                return Object.class;
            } else{
                return metaValue.getGetterType(prop.getChildren());
            }
        }else{
            if(map.get(name) != null){
                return map.get(name).getClass();
            }else{
                return Object.class;
            }
        }
    }

    /**
     * 获取set方法参数类型
     * @param name
     * @return
     */
    @Override
    public Class<?> getSetterType(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if(prop.hasNext()){
            MetaObject metaValue = this.metaObject.metaObjectForProperty(prop.getIndexedname());
            if(metaValue == SystemMetaObject.NULL_META_OBJECT){
                return Object.class;
            }else{
                return metaValue.getSetterType(prop.getChildren());
            }
        }else{
            if(map.get(name) != null){
                return map.get(name).getClass();
            }else{
                return Object.class;
            }
        }
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public boolean hasGetter(String name) {
        PropertyTokenizer prop = new PropertyTokenizer(name);
        if(prop.hasNext()){
            if(map.containsKey(prop.getChildren())){
                MetaObject metaObject = this.metaObject.metaObjectForProperty(prop.getIndexedname());
                if(metaObject == SystemMetaObject.NULL_META_OBJECT){
                    return true;
                } else{
                    return metaObject.hasGetter(prop.getChildren());
                }
            }else{
                return false;
            }
        }else{
            return map.containsKey(prop.getName());
        }
    }

    @Override
    public boolean hasSetter(String name) {
        return true;
    }

    @Override
    public MetaObject instantiatePropertyValue(String name, PropertyTokenizer propertyTokenizer,
                                               ObjectFactory objectFactory) {
        HashMap<String, Object> map = new HashMap<>();
        set(propertyTokenizer,map);
        return MetaObject.forObject(map,metaObject.getObjectFactory(),metaObject.getObjectWrapperFactory());
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
