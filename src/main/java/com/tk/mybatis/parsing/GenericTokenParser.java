package com.tk.mybatis.parsing;

import com.tk.mybatis.builder.SqlSourceBuilder;

/**
 * @Author liuht
 * @Date 2023/8/29 20:02
 * @Version 1.0
 * @Description 普通记号解析器 处理#{} 和 ${} 参数
 */
public class GenericTokenParser {
    //创建一个开始和结束的记号
    private final String openToken;
    private final String closeToken;

    //记号处理器
    private final TokenHandler handler;


    /**
     * "#{", "}",
     * @param openToken
     * @param closeToken
     * @param handler
     */
    public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
        this.openToken = openToken;
        this.closeToken = closeToken;
        this.handler = handler;
    }

    public String parse(String text){
        StringBuilder builder = new StringBuilder();
        if(text != null && text.length() > 0){
            char[] src = text.toCharArray();
            int offset = 0;

            int start = text.indexOf(openToken, offset);
            // #{favouriteSection,jdbcType=VARCHAR}
            // 这里是循环解析参数，参考GenericTokenParserTest,比如可以解析${first_name} ${initial} ${last_name} reporting.这样的字符串,里面有3个${}
            while(start > -1){
                // 判断一下 ${ 前面是不是反斜杠 这个逻辑在mybatis3.0.1之前是没有的
                //TODO 了解这里的解析逻辑
                //如果是就跳过解析，不进行变量替换
                if(start > 0 && src[start -1] == '\\'){
                    builder.append(src,offset,start-offset-1).append(openToken);
                    offset = start + openToken.length();
                }else {
                    //先拼接${之前的字符串
                    builder.append(src, offset, start - offset);
                    int end = text.indexOf(closeToken, start);
                    offset = start + openToken.length();
                    String content = new String(src, offset, end - offset);
                    //这里的content是打括号里面的字符串，然后通过调用handler.handlerToken进行处理，比如替换参数
                    builder.append(handler.handlerToken(content));
                    offset = end + closeToken.length();
                }
                start = text.indexOf(openToken,offset);
            }

            if(offset < src.length){
                builder.append(src,offset,src.length - offset);
            }
        }

        return builder.toString();
    }
}
