package com.tk.mybatis.session;

import java.io.Closeable;

/**
 * @Author liuht
 * @Date 2023/7/29 22:55
 * @Version 1.0
 * @Description 描述
 */
public interface SqlSession extends Closeable {
    /**
     * 根据指定的sql获取一条封装的对象数据
     * @param statement
     * @param <T>
     * @return
     */
    <T> T selectOne(String statement);

    /**
     * 根据执行的sql和参数获取一条封装的对象数据
     * @param statement
     * @param paramenter
     * @param <T>
     * @return
     */
    <T> T selectOne(String statement,Object paramenter);

    /**
     * 获取映射器
     * @param type
     * @param <T>
     * @return
     */
    <T> T getMapper(Class<T> type);

    /**
     * 获取mapper配置类
     * @return
     */
    Configuration getConfiguration();
}
