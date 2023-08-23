package com.tk.mybatis.datasource.unPooled;

import com.tk.mybatis.datasource.DataSourceFactory;
import com.tk.mybatis.reflection.MetaObject;
import com.tk.mybatis.reflection.SystemMetaObject;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @Author liuht
 * @Date 2023/8/4 15:23
 * @Version 1.0
 * @Description 描述
 */
public class UnPooledDataSourceFactory implements DataSourceFactory {
    protected DataSource dataSource;

    public UnPooledDataSourceFactory(){
        this.dataSource = new UnPooledDataSource();
    }

    @Override
    public void setProperties(Properties properties) {
        MetaObject metaObject = SystemMetaObject.forObject(dataSource);
        for(Object key : properties.keySet()){
            String propertyName = (String) key;
            if(metaObject.hasSetter(propertyName)){
                String value = (String) properties.get(propertyName);
                Object convertedValue = convertValue(metaObject,propertyName,value);
                metaObject.setValue(propertyName,convertedValue);
            }
        }
    }

    /**
     * 根据setter的类型，将配置文件中的值强转为相应的类型
     * @param metaObject
     * @param propertyName
     * @param value
     * @return
     */
    private Object convertValue(MetaObject metaObject, String propertyName, String value) {
        Object convertedValue = value;
        Class<?> targetType = metaObject.getSetterType(propertyName);
        if (targetType == Integer.class || targetType == int.class) {
            convertedValue = Integer.valueOf(value);
        } else if (targetType == Long.class || targetType == long.class) {
            convertedValue = Long.valueOf(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            convertedValue = Boolean.valueOf(value);
        }
        return convertedValue;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
