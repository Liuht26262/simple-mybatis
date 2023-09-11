package com.tk.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author liuht
 * @Date 2023/9/4 21:38
 * @Version 1.0
 * @Description 类型处理器
 */
public interface TypeHandler<T> {

    void setParameter(PreparedStatement ps,int index,T parameter,JdbcType jdbcType) throws SQLException;
}
