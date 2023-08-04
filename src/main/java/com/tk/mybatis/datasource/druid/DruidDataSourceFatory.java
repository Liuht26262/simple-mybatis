package com.tk.mybatis.datasource.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.tk.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @Author liuht
 * @Date 2023/8/3 10:44
 * @Version 1.0
 * @Description 描述
 */
public class DruidDataSourceFatory implements DataSourceFactory {
    private Properties properties;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public DataSource getDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(properties.getProperty("url"));
        dataSource.setDriverClassName(properties.getProperty("driver"));
        dataSource.setUsername(properties.getProperty("username"));
        dataSource.setPassword(properties.getProperty("password"));
        return dataSource;
    }
}
