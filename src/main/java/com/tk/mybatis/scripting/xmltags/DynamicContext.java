package com.tk.mybatis.scripting.xmltags;

import com.tk.mybatis.reflection.MetaObject;
import com.tk.mybatis.session.Configuration;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/8/28 21:04
 * @Version 1.0
 * @Description 动态上下文
 */
public class DynamicContext {
    public static final String PARAMETER_OBJECT_KEY = "_parameter";
    public static final String DATABASE_ID_KEY = "_databaseId";

    private final ContextMap bindings;
    private final StringBuilder sqlBuilder = new StringBuilder();
    private int uniqueNumber = 0;

    static{
        // 定义属性->getter方法映射，ContextMap到ContextAccessor的映射，注册到ognl运行时
        // 参考http://commons.apache.org/proper/commons-ognl/developer-guide.html
        OgnlRuntime.setPropertyAccessor(ContextMap.class, new ContextAccessor());
        // 将传入的参数对象统一封装为ContextMap对象（继承了HashMap对象），
        // 然后Ognl运行时环境在动态计算sql语句时，
        // 会按照ContextAccessor中描述的Map接口的方式来访问和读取ContextMap对象，获取计算过程中需要的参数。
        // ContextMap对象内部可能封装了一个普通的POJO对象，也可以是直接传递的Map对象，当然从外部是看不出来的，因为都是使用Map的接口来读取数据。
    }


    public DynamicContext(Configuration configuration, Object parameterObject) {
        //绝大多数调用的地方parameterObject为null
        if(parameterObject != null && !(parameterObject instanceof Map)){
            MetaObject metaObject = configuration.newMetaObject(parameterObject);
            bindings = new ContextMap(metaObject);
        }else {
            bindings = new ContextMap(null);
        }
        bindings.put(PARAMETER_OBJECT_KEY,parameterObject);
        bindings.put(DATABASE_ID_KEY,configuration.getDatabaseId());
    }

    public Map<String, Object> getBindings() {
        return bindings;
    }

    public void bind(String name, Object value) {
        bindings.put(name, value);
    }

    public void appendSql(String sql) {
        sqlBuilder.append(sql);
        sqlBuilder.append(" ");
    }

    public String getSql(){
        return sqlBuilder.toString().trim();
    }

    public int getUniqueNumber(){
        return uniqueNumber++;
    }

    //上下文Map 静态内部类
    static class ContextMap extends HashMap<String,Object>{
        private static final long serialVersionUID = 6372601501966151582L;

        private MetaObject parameterObject;

        public ContextMap(MetaObject parameterMetaObject){
            this.parameterObject = parameterMetaObject;
        }

        @Override
        public Object get(Object key){
            String strKey = (String)key;

            //现在map里找
            if(super.containsKey(key)){
                return super.get(key);
            }

            //如果没找到，在用ognl表达式进行取值
            // 如person[0].birthdate.year
            if(parameterObject != null){
                return parameterObject.getValue(strKey);
            }

            return null;
        }
    }


    //上下文访问类 静态内部类 实现ognl的PropertyAccessor
    static class ContextAccessor implements PropertyAccessor{
        @Override
        public Object getProperty(Map context, Object target, Object name){
            Map map = (Map) target;

            Object result = map.get(name);
            if(result != null){
                return result;
            }

            Object parameterOject = map.get(PARAMETER_OBJECT_KEY);
            if(parameterOject instanceof Map){
                return ((Map)parameterOject).get(name);
            }

            return null;
        }

        @Override
        public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
            Map<Object, Object> map = (Map<Object, Object>) target;
            map.put(name,value);
        }

        @Override
        public String getSourceAccessor(OgnlContext ognlContext, Object o, Object o1) {
            return null;
        }

        @Override
        public String getSourceSetter(OgnlContext ognlContext, Object o, Object o1) {
            return null;
        }
    }



}
