package com.tk.mybatis.builder;

import com.tk.mybatis.session.Configuration;

/**
 * @Author liuht
 * @Date 2023/8/1 14:12
 * @Version 1.0
 * @Description 描述
 */
public abstract class BaseBuilder {
    protected Configuration configuration;
    public BaseBuilder(Configuration configuration){
        this.configuration = configuration;
    }
    public Configuration getConfiguration(){
        return this.configuration;
    }
}
