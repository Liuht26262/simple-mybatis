package com.tk.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author liuht
 * @Date 2023/9/4 22:04
 * @Version 1.0
 * @Description 描述
 */
public class StringTypeHandler extends BaseTypeHandler<String>{

    @Override
    protected void setNonNullParmeter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i,parameter);
    }
}
