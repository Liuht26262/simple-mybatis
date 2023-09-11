package com.tk.mybatis.dao;

import com.tk.mybatis.po.User;

/**
 * @Author liuht
 * @Date 2023/7/29 17:17
 * @Version 1.0
 * @Description 描述
 */
public interface IUserDao {
    /**
     * 查询用户信息
     * @param id
     * @return
     */
    User queryUserById(Long id);

}
