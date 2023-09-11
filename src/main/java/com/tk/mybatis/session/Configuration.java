package com.tk.mybatis.session;

import com.tk.mybatis.binding.MapperRegistry;
import com.tk.mybatis.datasource.Pooled.PooledDataSourceFactory;
import com.tk.mybatis.datasource.druid.DruidDataSourceFatory;
import com.tk.mybatis.datasource.unPooled.UnPooledDataSourceFactory;
import com.tk.mybatis.executor.BaseExecutor;
import com.tk.mybatis.executor.Executor;
import com.tk.mybatis.executor.SimpleExecutor;
import com.tk.mybatis.executor.parameter.ParameterHandler;
import com.tk.mybatis.executor.resultset.DefaultResultSetHandler;
import com.tk.mybatis.executor.resultset.ResultSetHandler;
import com.tk.mybatis.executor.statement.PrepareStatementHandler;
import com.tk.mybatis.executor.statement.StatementHandler;
import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.Environment;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.reflection.MetaObject;
import com.tk.mybatis.reflection.factory.DefaultObjectFactory;
import com.tk.mybatis.reflection.factory.ObjectFactory;
import com.tk.mybatis.reflection.wrapper.DefaultObjectWrapperFactory;
import com.tk.mybatis.reflection.wrapper.ObjectWrapperFactory;
import com.tk.mybatis.scripting.LanguageDriverRegistry;
import com.tk.mybatis.scripting.xmltags.XMLLanguageDriver;
import com.tk.mybatis.transaction.Transaction;
import com.tk.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.tk.mybatis.type.TypeAliasRegistry;
import com.tk.mybatis.type.TypeHandlerRegistry;
import sun.plugin2.main.server.ResultHandler;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();

    //对象工厂和对象包装器
    protected ObjectFactory objectFactory = new DefaultObjectFactory();
    protected ObjectWrapperFactory objectWrapperFactory = new DefaultObjectWrapperFactory();

    protected String databaseId;


    /**
     * 资源加载集合
     */
    protected final Set<String> loaderResources = new HashSet();

    /**
     * 类型处理器注册机
     */
    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();


    /**
     * 在创建的时候将事务以及数据源都注册到别名注册机中，方便后面直接从Configuration中获取
     */
    public Configuration() {
        typeAliasRegistry.registryAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registryAlias("DRUID", DruidDataSourceFatory.class);
        typeAliasRegistry.registryAlias("UNPOOLED", UnPooledDataSourceFactory.class);
        typeAliasRegistry.registryAlias("POOLED", PooledDataSourceFactory.class);
        languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
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

    /**
     * 创建结果集处理器
     */
    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        return new DefaultResultSetHandler(executor, mappedStatement, boundSql);
    }

    public StatementHandler newStatementHandler(SimpleExecutor executor, MappedStatement mappedStatement,
                                                Object parameter, ResultHandler handler, BoundSql boundSql) {
        return new PrepareStatementHandler(executor,mappedStatement,parameter,handler,boundSql);
    }

    public Executor newExecutor(Transaction transaction) {
        return new SimpleExecutor(this,transaction);
    }

    public boolean isResourceLoaded(String resource) {
        if(loaderResources.contains(resource)){
            return true;
        }
        return false;
    }

    public void addLoadedResources(String resource){
        loaderResources.add(resource);
    }

    public LanguageDriverRegistry getLanguageRegistry() {
        return languageRegistry;
    }

    //创建元对象
    public MetaObject newMetaObject(Object parameterObject) {
        return MetaObject.forObject(parameterObject,objectFactory,objectWrapperFactory);
    }

    public Object getDatabaseId() {
        return databaseId;
    }

    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameter, BoundSql boundSql) {
        // 创建参数处理器
        ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameter, boundSql);

        return parameterHandler;
    }

    //类型处理器注册机
    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }
}
