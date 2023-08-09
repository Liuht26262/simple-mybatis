package com.tk.mybatis.session.defaults;

import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.Environment;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @Author liuht
 * @Date 2023/7/29 23:14
 * @Version 1.0
 * @Description 描述
 */
public class DefaultSqlSession implements SqlSession {
    private Configuration configuration;
    private static final Logger log = LoggerFactory.getLogger(DefaultSqlSession.class);

    public DefaultSqlSession(Configuration configuration){
        this.configuration = configuration;
    }

    @Override
    public <T> T selectOne(String statement) {
        return (T) ("你被代理了！" + "方法：" + statement);
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        MappedStatement mappedstatement = configuration.getMappedstatement(statement);
        Environment environment = configuration.getEnvironment();
        Connection connection = null;
        try {
            connection = environment.getDataSource().getConnection();
            BoundSql boundSql = mappedstatement.getBoundSql();
            PreparedStatement preparedStatement = connection.prepareStatement(boundSql.getSql());
            preparedStatement.setLong(1,Long.parseLong(((Object[])parameter)[0].toString()));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<T> result = resultSetToObject(resultSet,Class.forName(boundSql.getResultType()));
            return result.get(0);
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
            return null;
        } finally {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
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

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type,this);
    }

    @Override
    public Configuration getConfiguration(){
        return this.configuration;
    }
}
