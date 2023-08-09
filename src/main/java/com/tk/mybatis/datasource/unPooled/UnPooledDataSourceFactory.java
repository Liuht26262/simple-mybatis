package com.tk.mybatis.datasource.unPooled;

import com.tk.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @Author liuht
 * @Date 2023/8/4 15:23
 * @Version 1.0
 * @Description 描述
 */
public class UnPooledDataSourceFactory implements DataSourceFactory {
    protected Properties properties;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        UnPooledDataSource unPooledDataSource = new UnPooledDataSource();
        unPooledDataSource.setDriverStr(properties.getProperty("driver"));
        unPooledDataSource.setUrl(properties.getProperty("url"));
        unPooledDataSource.setUsername(properties.getProperty("username"));
        unPooledDataSource.setPassword(properties.getProperty("password"));
        return unPooledDataSource;
    }
}
