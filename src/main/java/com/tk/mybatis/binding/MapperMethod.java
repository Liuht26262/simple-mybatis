package com.tk.mybatis.binding;

import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.mapping.SqlCommandType;
import com.tk.mybatis.session.Configuration;
import com.tk.mybatis.session.SqlSession;
import org.apache.catalina.util.ParameterMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @Author liuht
 * @Date 2023/8/1 22:30
 * @Version 1.0
 * @Description 描述
 */
public class MapperMethod {
    private static final Logger log = LoggerFactory.getLogger(MapperMethod.class);
    private final SqlCommand sqlCommand;
    private final MethodSignature method;

    public <T> MapperMethod(Class<T> mapperInterface, Method method, Configuration configuration) {
        this.sqlCommand = new SqlCommand(mapperInterface,method,configuration);
        this.method = new MethodSignature(configuration,method);
    }

    public Object execute(SqlSession sqlSession,Object[] args){
        Object result = null;
        switch (sqlCommand.getCommandType()){
            case SELECT:
                log.info("执行查询语句");
                Object param = method.convertArgsToSqlCommandParams(args);
                result = sqlSession.selectOne(sqlCommand.getName(),param);
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

    /**
     * 方法签名
     */
    public static class MethodSignature {
        private final SortedMap<Integer, String> params;

        public MethodSignature(Configuration configuration,Method method){
            this.params = Collections.unmodifiableSortedMap(getParams(method));
        }

        /**
         * 参数转换
         * @param args
         * @return
         */
        public Object convertArgsToSqlCommandParams(Object[] args){
            final int paramCount = params.size();
            if(args == null || paramCount == 0){
                //如果没有参数
                return null;
            }else if(paramCount == 1){
                return args[params.keySet().iterator().next().intValue()];
            }else {
                //返回一个paramMap, 修改参数名，参数名就是其位置
                final Map<String,Object> param = new ParamMap<Object>();
                int i = 0;
                for(Map.Entry<Integer,String> entry : params.entrySet()){
                    // 1.先加一个#{0},#{1},#{2}...参数
                    param.put(entry.getValue(),args[entry.getKey().intValue()]);

                    final String genericParamName = "params" + (i+1);
                    if(!param.containsKey(genericParamName)){
                        /*
                         * 2.再加一个#{param1},#{param2}...参数
                         * 这样可以传递多个参数给一个映射器方法
                         * 默认情况下它们将会以它们在参数列表中的位置来命名,比如:#{param1},#{param2}等。
                         * 如果你改变参数的名称(只在多参数情况下) ,那么可以在参数上使用@Param(“paramName”)注解。
                         */
                        param.put(genericParamName,args[entry.getKey()]);
                    }
                    i++;
                }
                return param;
            }
        }

        /**
         * 创建一个有序Map,这里也是为了在sql参数进行替换的时候能够有序
         * @param method
         * @return
         */
        private SortedMap<Integer,String> getParams(Method method) {
            final SortedMap<Integer,String> params = new TreeMap<>();
            final Class<?>[] argsType = method.getParameterTypes();

            for(int i = 0;i< argsType.length;i++){
                String paramsName = String.valueOf(argsType[i]);
                params.put(i,paramsName);
            }

            return params;
        }

        /**
         * 参数Map,静态内部类，重写get方法，如果没有key就报错
         * @param <V>
         */
        public static  class ParamMap<V> extends HashMap<String, V> {
            private static final long serialVersionUID = -2212268410512236756L;

            @Override
            public V get(Object key) {
                if(!super.containsKey(key)){
                    throw new RuntimeException("Parameter '" + key + "' not found. Available parameters are " + keySet());
                }

                return super.get(key);
            }
        }
    }
}
