package com.tk.mybatis.binding;

import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.session.SqlSession;
import com.tk.mybatis.util.ClassScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Author liuht
 * @Date 2023/7/29 20:50
 * @Version 1.0
 * @Description mapper注册及
 */
public class MapperRegistry {
    private Configuration configuration;
    private final Map<Class<?>,MapperProxyFactory<?>> proxyFactoryMap = new HashMap<>();

    public MapperRegistry(){};
    public MapperRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    /**
     * 获取对应的映射器
     * @param type
     * @param sqlSession
     * @param <T>
     * @return
     */
    public <T> T getMapper(Class<T> type, SqlSession sqlSession){
        final MapperProxyFactory<T> proxyFactory = (MapperProxyFactory<T>) proxyFactoryMap.get(type);
        if(proxyFactory == null){
            throw new RuntimeException("the type "+type+" is not exist MapperProxyFactory");
        }
        return proxyFactory.newInstance(sqlSession);
    }

    /**
     * 将mapper接口注册到注册机中
     * @param type mapper接口
     * @param <T>
     */
    public <T> void addMapper(Class<T> type){
        /** 确认是不是接口 */
        if(type.isInterface()){
            if(hasMapper(type)){
                throw new RuntimeException("type "+type+" is already know to the mapperRestry");
            }
            proxyFactoryMap.put(type,new MapperProxyFactory<>(type));
        }
    }

    /**
     * 注册包路径下的所有mapper
     * @param packgePath
     */
    public void addMappers(String packgePath){
        Set<Class<?>> mapperSet = ClassScanner.scanPackge(packgePath);
        for (Class<?> mapper : mapperSet){
            addMapper(mapper);
        }
    }

    /**
     * 查看mappe注册机中是否存在
     * @param type
     * @param <T>
     * @return
     */
    public <T> boolean hasMapper(Class<T> type) {
        if(proxyFactoryMap.containsKey(type)){
            return true;
        }
        return false;
    }
}
