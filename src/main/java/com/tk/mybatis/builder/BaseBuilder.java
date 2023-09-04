package com.tk.mybatis.builder;

import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.type.TypeAliasRegistry;

/**
 * @Author liuht
 * @Date 2023/8/1 14:12
 * @Version 1.0
 * @Description 描述
 */
public abstract class BaseBuilder {
    protected Configuration configuration;
    protected TypeAliasRegistry typeAliasRegistry;

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public void setTypeAliasRegistry(TypeAliasRegistry typeAliasRegistry) {
        this.typeAliasRegistry = typeAliasRegistry;
    }

    public BaseBuilder(Configuration configuration){
        this.configuration = configuration;
        this.typeAliasRegistry = configuration.getTypeAliasRegistry();
    }
    public Configuration getConfiguration(){
        return this.configuration;
    }

    protected Class<?> resolveAlias(String alias) {
        return typeAliasRegistry.resolveAlias(alias);
    }
}
