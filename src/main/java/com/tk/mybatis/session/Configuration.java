package com.tk.mybatis.session;

import com.tk.mybatis.binding.MapperRegistry;
import com.tk.mybatis.mapping.MappedStatement;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/7/31 16:54
 * @Version 1.0
 * @Description mapper配置类
 */
public class Configuration {
    /**
     * mapper注册机
     */
    protected MapperRegistry mapperRegistry = new MapperRegistry(this);

    /**
     * Mapper方法对应的sql配置Map
     * key methodId   namespaceId+methodId
     * value 对应的sql配置
     */
    protected Map<String, MappedStatement> mappedStatementMap = new HashMap<>();


    public <T> void addMapper(Class<T> type){
        mapperRegistry.addMapper(type);
    }

    public void addMappers(String packagePath){
        mapperRegistry.addMappers(packagePath);
    }

    public boolean hasMapper(Class<?> type){
        return mapperRegistry.hasMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession){
        return mapperRegistry.getMapper(type,sqlSession);
    }

    public void addMappedstatement(MappedStatement ms){
        System.out.println(ms.getId());
        mappedStatementMap.put(ms.getId(),ms);
    }

    public MappedStatement getMappedstatement(String id){
        return mappedStatementMap.get(id);
    }

}
