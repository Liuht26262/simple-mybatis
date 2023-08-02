package com.tk.mybatis.binding;

import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.mapping.SqlCommandType;
import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * @Author liuht
 * @Date 2023/8/1 22:30
 * @Version 1.0
 * @Description 描述
 */
public class MapperMethod {
    private static final Logger log = LoggerFactory.getLogger(MapperMethod.class);
    private final SqlCommand sqlCommand;

    public <T> MapperMethod(Class<T> mapperInterface, Method method, Configuration configuration) {
        this.sqlCommand = new SqlCommand(mapperInterface,method,configuration);
    }

    public Object execute(SqlSession sqlSession,Object[] args){
        Object result = null;
        switch (sqlCommand.getCommandType()){
            case SELECT:
                log.info("执行查询语句");
                result = sqlSession.selectOne(sqlCommand.getName(),args);
                break;
            case INSERT:
                log.info("执行新增语句");
                break;
            case UPDATE:
                log.info("执行更新语句");
                break;
            case DELETE:
                log.info("执行删除语句");
                break;
            default:
                throw new RuntimeException("Unknow executing method for "+sqlCommand.getName());
        }

        return result;
    }

    private static class SqlCommand {
        private String name;
        private SqlCommandType commandType;

        public <T> SqlCommand(Class<T> mapperInterface, Method method, Configuration configuration) {
            String statement = mapperInterface.getName() + "." + method.getName();
            MappedStatement mappedStatement = configuration.getMappedstatement(statement);
            this.name = mappedStatement.getId();
            this.commandType = mappedStatement.getSqlCommandType();
        }

        public String getName(){
            return this.name;
        }

        public SqlCommandType getCommandType(){
            return this.commandType;
        }
    }
}
