package com.tk.mybatis.type;

import com.tk.mybatis.session.Configuration;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author liuht
 * @Date 2023/9/4 21:59
 * @Version 1.0
 * @Description 类型处理器的基类
 */
public abstract class BaseTypeHandler<T> implements TypeHandler<T>{

    protected Configuration configuration;

    public void setConfiguration(Configuration configuration){
        this.configuration = configuration;
    }

    @Override
    public void setParameter(PreparedStatement ps, int index, T parameter, JdbcType jdbcType) throws SQLException {
        //定义抽象方法，由子类实现不同类型的属性设置
        setNonNullParmeter(ps,index,parameter,jdbcType);
    }

    protected abstract void setNonNullParmeter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException;
}
