package com.tk.mybatis.type;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/9/4 21:36
 * @Version 1.0
 * @Description 类型处理器注册机
 */
public class TypeHandlerRegistry {

    private final Map<JdbcType, TypeHandler<?>> JDBC_TYPE_HANDLER_MAP = new EnumMap<>(JdbcType.class);
    private final Map<Type,Map<JdbcType, TypeHandler<?>>> TYPE_HANDLER_MAP = new HashMap<>();
    private final Map<Class<?>, TypeHandler<?>> ALL_TYPE_HANDLER_MAP = new HashMap<>();

    public TypeHandlerRegistry(){
        // 注册原始类型
        // TODO 这里暂时只实现了两种类型的注册，后面可继续扩展其他类型的注册
        register(Long.class, new LongTypeHandler());
        register(long.class, new LongTypeHandler());

        register(String.class, new StringTypeHandler());
        register(String.class, JdbcType.CHAR, new StringTypeHandler());
        register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
    }

    private <T> void register(Type javaType, TypeHandler<? extends T> typeHandler){
        register(javaType,null,typeHandler);
    }

    private <T> void register(Type javaType, JdbcType jdbcType, TypeHandler<? extends T> typeHandler) {
        if(null != javaType){
            Map<JdbcType, TypeHandler<?>> map = TYPE_HANDLER_MAP.computeIfAbsent(javaType, k -> new HashMap<>());
            map.put(jdbcType, typeHandler);
        }
        ALL_TYPE_HANDLER_MAP.put(typeHandler.getClass(),typeHandler);
    }


    public <T> TypeHandler<T> getTypeHandler(Class<T> type, JdbcType jdbcType){
        return getTypeHandler((Type)type,jdbcType);
    }

    /**
     * 获取类型处理器
     * @param type
     * @param jdbcType
     * @param <T>
     * @return
     */
    private <T> TypeHandler<T> getTypeHandler(Type type, JdbcType jdbcType){
        Map<JdbcType, TypeHandler<?>> jdbcHandlerMap = TYPE_HANDLER_MAP.get(type);
        TypeHandler<?> handler = null;
        if(jdbcHandlerMap != null){
            handler = jdbcHandlerMap.get(jdbcType);
            if(handler == null){
                handler = jdbcHandlerMap.get(null);
            }
        }

        return (TypeHandler<T>) handler;
    }

    public boolean hasTypeHandler(Class<?> javaType){
        return hasTypeHandler(javaType,null);
    }

    private boolean hasTypeHandler(Class<?> javaType, JdbcType jdbcType) {
        return javaType != null && getTypeHandler((Type)javaType, jdbcType) != null;
    }
}
