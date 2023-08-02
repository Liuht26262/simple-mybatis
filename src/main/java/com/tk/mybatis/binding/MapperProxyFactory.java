package com.tk.mybatis.binding;

import com.tk.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/7/29 13:48
 * @Version 1.0
 * @Description 描述
 */
public class MapperProxyFactory<T> {
    private final Class<T> mapperInterface;
    private Map<Method,MapperMethod> mapperMethodMap = new HashMap<>();

    public MapperProxyFactory(Class<T> mapperInterface){
        this.mapperInterface = mapperInterface;
    }

    public Map<Method,MapperMethod> getMapperMethodMap(){
        return this.mapperMethodMap;
    }

    @SuppressWarnings("unchecked")
    public T newInstance(SqlSession sqlSession){
        MapperProxy<T> mapperProxy = new MapperProxy<>(mapperInterface, sqlSession,mapperMethodMap);
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(),new Class[]{mapperInterface},
                mapperProxy);

    }

}
