package com.tk.mybatis.reflection.property;

import java.util.Locale;

/**
 * @Author liuht
 * @Date 2023/8/14 21:40
 * @Version 1.0
 * @Description 描述
 */
public class PropertyNamer {

    private PropertyNamer(){

    }

    /**
     * 将方法转换为属性
     * 相当去去掉get、is或者set获得属性名
     * @param name
     * @return
     */
    public static String methodToProperty(String name){
        if(name.startsWith("is")){
            name = name.substring(2);
        }else if(name.startsWith("get") || name.startsWith("set")){
            name = name.substring(3);
        }else {
            throw new RuntimeException("Error Parsing property name is"+name+",Didn't start with 'set' or 'get' or ''is");
        }

        if(name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))){
            name = name.substring(0,1).toLowerCase(Locale.ENGLISH)+name.substring(1);
        }

        return name;
    }


    public static boolean isGetter(String name){
        return name.startsWith("get") || name.startsWith("is");
    }

    public static boolean isSetter(String name){
        return name.startsWith("set");
    }

    public static boolean isProperty(String name){
        return name.startsWith("get") || name.startsWith("is") || name.startsWith("set");
    }
}
