package com.tk.mybatis.mapping;

/**
 * @Author liuht
 * @Date 2023/8/28 20:53
 * @Version 1.0
 * @Description 描述
 */
public interface SqlSource {
    BoundSql getBoundSql(Object parameterObject);
}
