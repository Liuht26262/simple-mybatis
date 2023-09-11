package com.tk.mybatis.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author liuht
 * @Date 2023/9/4 21:58
 * @Version 1.0
 * @Description 描述
 */
public class LongTypeHandler extends BaseTypeHandler<Long>{

    @Override
    protected void setNonNullParmeter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType) throws SQLException {
        ps.setLong(i,parameter);
    }
}
