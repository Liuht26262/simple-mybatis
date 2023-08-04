package com.tk.mybatis.mapping;


import com.tk.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;


/**
 * @Author liuht
 * @Date 2023/8/3 11:27
 * @Version 1.0
 * @Description 环境参数
 */
public class Environment {
    //环境id
    private String id;
    //事务工厂
    private TransactionFactory transactionFactory;
    //数据源
    private DataSource dataSource;


    public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
        this.id = id;
        this.transactionFactory = transactionFactory;
        this.dataSource = dataSource;
    }


    public static class Builder{
        private String id;
        private TransactionFactory transactionFactory;
        private DataSource dataSource;

        public Builder(String id){
            this.id = id;
        }

        public Builder transactionFactory(TransactionFactory transactionFactory){
            this.transactionFactory = transactionFactory;
            return this;
        }

        public String id(){
            return this.id;
        }

        public Builder datasource(DataSource dataSource){
            this.dataSource = dataSource;
            return this;
        }

        public Environment build(){
            return new Environment(id,transactionFactory,dataSource);
        }
    }

    public String getId() {
        return id;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
