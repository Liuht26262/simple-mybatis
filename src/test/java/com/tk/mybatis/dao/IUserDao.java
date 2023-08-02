package com.tk.mybatis.dao;

/**
 * @Author liuht
 * @Date 2023/7/29 17:17
 * @Version 1.0
 * @Description 描述
 */
public interface IUserDao {
    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    String queryUserById(String userId);

    /**
     * 查询用户年龄
     * @param userId
     * @return
     */
    String queryUserAge(String userId);
}
