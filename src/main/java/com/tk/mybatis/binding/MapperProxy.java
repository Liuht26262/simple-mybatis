package com.tk.mybatis.binding;

import com.tk.mybatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/7/29 16:48
 * @Version 1.0
 * @Description 描述
 */
public class MapperProxy<T> implements InvocationHandler, Serializable {
    private static final long serialVerionUID = -73286382729179287L;

    private SqlSession sqlSession;
    private final Class<T> mapperInterface;

    /**
     * sql语句缓存Map
     * key Method
     * value MapperMethod
     */
    private Map<Method,MapperMethod> mapperMethodCache;

    public MapperProxy(Class<T> mapperInterface, SqlSession sqlSession, Map<Method, MapperMethod> mapperMethodMap) {
        this.mapperInterface = mapperInterface;
        this.sqlSession = sqlSession;
        this.mapperMethodCache = mapperMethodMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(Object.class.equals(method.getDeclaringClass())){
            //TODO 注意这里invoke方法的参数是被代理对象，而不是代理对象
            return method.invoke(this,args);
        }else {
            final MapperMethod mapperMethod = cacheMapperMethod(method);
            return mapperMethod.execute(sqlSession,args);
        }
    }

    private MapperMethod cacheMapperMethod(Method method) {
        MapperMethod mapperMethod = mapperMethodCache.get(method);
        if(mapperMethod == null){
            //创建MapperMethod加入到缓存中
            mapperMethod = new MapperMethod(mapperInterface,method,sqlSession.getConfiguration());
            mapperMethodCache.put(method,mapperMethod);
        }
        return mapperMethod;
    }
}
