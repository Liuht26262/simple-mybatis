package com.tk.mybatis.datasource.Pooled;

import com.tk.mybatis.datasource.unPooled.UnPooledDataSourceFactory;

import javax.sql.DataSource;

/**
 * @Author liuht
 * @Date 2023/8/8 14:26
 * @Version 1.0
 * @Description 描述
 */
public class PooledDataSourceFactory extends UnPooledDataSourceFactory {

    @Override
    public DataSource getDataSource() {
        PooledDataSource pooledDataSource = new PooledDataSource();
        pooledDataSource.setDriver(properties.getProperty("driver"));
        pooledDataSource.setUrl(properties.getProperty("url"));
        pooledDataSource.setUsername(properties.getProperty("username"));
        pooledDataSource.setPassword(properties.getProperty("password"));
        return pooledDataSource;
    }
}
