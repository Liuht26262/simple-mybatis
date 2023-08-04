package com.tk.mybatis.type;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/8/2 16:05
 * @Version 1.0
 * @Description 类型别名注册器
 */
public class TypeAliasRegistry {

    private Map<String,Class<?>> typeAliasMap = new HashMap<>();

    public TypeAliasRegistry(){
        //基本类型包装
        registryAlias("int",Integer.class);
        registryAlias("boolean", Boolean.class);
        registryAlias("char",Character.class);
        registryAlias("byte",Byte.class);
        registryAlias("short",Short.class);
        registryAlias("long",Long.class);
        registryAlias("float",Float.class);
        registryAlias("double",Double.class);

        //注册系统内部的类型别名
        registryAlias("string",String.class);
    }

    public void registryAlias(String alias, Class<?> clazz) {
        String key = alias.toLowerCase(Locale.ENGLISH);
        typeAliasMap.put(key,clazz);
    }

    public <T> Class<T> resolveAlias(String alias){
        String key = alias.toLowerCase(Locale.ENGLISH);
        return (Class<T>)typeAliasMap.get(key);
    }
}
