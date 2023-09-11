package com.tk.mybatis.executor.statement;

import com.tk.mybatis.executor.Executor;
import com.tk.mybatis.executor.parameter.ParameterHandler;
import com.tk.mybatis.executor.resultset.ResultSetHandler;
import com.tk.mybatis.mapping.BoundSql;
import com.tk.mybatis.mapping.MappedStatement;
import com.tk.mybatis.session.Configuration;
import sun.plugin2.main.server.ResultHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @Author liuht
 * @Date 2023/8/9 21:28
 * @Version 1.0
 * @Description 描述
 */
public abstract class BaseStatementHandler implements StatementHandler{
    protected final Configuration configuration;
    protected final Executor executor;
    protected final MappedStatement mappedStatement;

    protected final Object parameter;
    protected final ResultSetHandler resultSetHandler;
    protected final ParameterHandler parameterHandler;

    protected BoundSql boundSql;

    public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameter,
                                ResultHandler resultHandler, BoundSql boundSql) {
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.parameter = parameter;
        this.boundSql = boundSql;
        this.resultSetHandler = configuration.newResultSetHandler(executor,mappedStatement,boundSql);
        this.parameterHandler = configuration.newParameterHandler(mappedStatement,parameter,boundSql);
    }

    /**
     * 语句准备
     * @param connection
     * @return
     * @throws SQLException
     */
    @Override
    public Statement prepare(Connection connection) throws SQLException {
        Statement statement;
        try{
            //实例化Statement
            statement = instantiateStatement(connection);
            //TODO 参数化配置，可扩展抽取
            //查询超时时间
            statement.setQueryTimeout(350);
            statement.setFetchSize(10000);
            return statement;
        }catch (Exception e){
            throw new RuntimeException("Error prepareing Statement,cause:"+e.getMessage());
        }

    }

    /**
     * 初始化
     * @param connection
     * @return
     * @throws SQLException
     */
    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;

}
