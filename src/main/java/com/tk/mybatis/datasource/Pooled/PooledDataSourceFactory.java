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

    public PooledDataSourceFactory(){
        this.dataSource = new PooledDataSource();
    }

}
