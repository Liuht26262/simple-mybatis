package com.tk.mybatis.parsing;

/**
 * @Author liuht
 * @Date 2023/8/29 20:01
 * @Version 1.0
 * @Description 记号处理器
 */
public interface TokenHandler {

    String handlerToken(String content);
}
