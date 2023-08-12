package com.tk.mybatis.executor.resultset;

import com.tk.mybatis.executor.Executor;
import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.MappedStatement;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author liuht
 * @Date 2023/8/9 22:13
 * @Version 1.0
 * @Description 描述
 */
public class DefaultResultSetHandler implements ResultSetHandler{
    private BoundSql boundSql;

    public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, BoundSql boundSql) {
        this.boundSql = boundSql;
    }

    @Override
    public <E> List<E> handleResultSet(Statement statement) throws SQLException {
        ResultSet resultSet = statement.getResultSet();
        try{
            Class<?> resultType = Class.forName(boundSql.getResultType());
            return  resultSetToObject(resultSet,resultType);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * sql执行结果转换封装
     * @param resultSet 执行结果
     * @param clazz do类型
     * @param <T>   返回类型
     * @return
     */
    private <T> List<T> resultSetToObject(ResultSet resultSet, Class<?> clazz) {
        List<T> resultList = new ArrayList<>();
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
//            log.info("当前class [{}] 有[{}]列属性",clazz.getClass(),columnCount);
            //遍历行值
            while (resultSet.next()){
                T obj = (T)clazz.newInstance();
                for (int i = 1; i <= columnCount; i++) {
                    Object object = resultSet.getObject(i);
                    String columnName = metaData.getColumnName(i);
                    String methodName = "set"+columnName.substring(0,1).toUpperCase()+columnName.substring(1);
//                    log.info("当前正在转换的方法为[{}]",methodName);
                    Method method;
                    if(object instanceof Date){
                        method = clazz.getMethod(methodName,Date.class);
                    }else {
                        method = clazz.getMethod(methodName,object.getClass());
                    }

                    method.invoke(obj,object);
                }
                resultList.add(obj);
            }
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
        return resultList;
    }
}
