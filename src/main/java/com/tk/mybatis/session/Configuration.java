package com.tk.mybatis.session;

import com.tk.mybatis.binding.MapperRegistry;
import com.tk.mybatis.datasource.Pooled.PooledDataSourceFactory;
import com.tk.mybatis.datasource.druid.DruidDataSourceFatory;
import com.tk.mybatis.datasource.unPooled.UnPooledDataSourceFactory;
import com.tk.mybatis.mapping.Environment;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.tk.mybatis.type.TypeAliasRegistry;


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


    /**
     * 环境
     */
    protected Environment environment;

    /**
     * 别名注册机
     */
    protected TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();


    /**
     * 在创建的时候将事务以及数据源都注册到别名注册机中，方便后面直接从Configuration中获取
     */
    public Configuration() {
        typeAliasRegistry.registryAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registryAlias("DRUID", DruidDataSourceFatory.class);
        typeAliasRegistry.registryAlias("UNPOOLED", UnPooledDataSourceFactory.class);
        typeAliasRegistry.registryAlias("POOLED", PooledDataSourceFactory.class);
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public void addMappers(String packagePath) {
        mapperRegistry.addMappers(packagePath);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public void addMappedstatement(MappedStatement ms) {
        mappedStatementMap.put(ms.getId(), ms);
    }

    public MappedStatement getMappedstatement(String id) {
        return mappedStatementMap.get(id);
    }


    public Environment getEnvironment() {
        return environment;
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }
}
