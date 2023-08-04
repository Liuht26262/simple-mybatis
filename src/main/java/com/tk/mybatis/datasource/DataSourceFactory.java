package com.tk.mybatis.datasource;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * @Author liuht
 * @Date 2023/8/3 10:40
 * @Version 1.0
 * @Description 描述
 */
public interface DataSourceFactory {

    void setProperties(Properties properties);

    DataSource getDataSource();
}
