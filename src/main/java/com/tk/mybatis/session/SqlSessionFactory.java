package com.tk.mybatis.session;


/**
 * @Author liuht
 * @Date 2023/7/29 20:50
 * @Version 1.0
 * @Description 描述
 */
public interface SqlSessionFactory {
    SqlSession openSession();
}
