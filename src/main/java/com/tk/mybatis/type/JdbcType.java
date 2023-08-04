package com.tk.mybatis.type;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/8/3 15:54
 * @Version 1.0
 * @Description Jdbc类型枚举
 */
public enum JdbcType {
    //Integer
    INTEGER(Types.INTEGER),
    //float
    FLOAT(Types.FLOAT),
    //double
    DOUBLE(Types.DOUBLE),
    //decimal
    DECIMAL(Types.DECIMAL),
    //varchar
    VARCHAR(Types.VARCHAR),
    //timeStamp
    TIMESTAMP(Types.TIMESTAMP)
    ;
    public int typeCode;
    public static Map<Integer,JdbcType> codeLookup = new HashMap<>();

    JdbcType(int typeCode) {
        this.typeCode = typeCode;
    }

    public int getTypeCode() {
        return typeCode;
    }

    static{
        for(JdbcType jdbcType : JdbcType.values()){
            codeLookup.put(jdbcType.getTypeCode(), jdbcType);
        }
    }

    public static JdbcType getJdbcType(int code){
        return codeLookup.get(code);
    }
}
