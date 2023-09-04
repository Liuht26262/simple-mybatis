package com.tk.mybatis.scripting;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author liuht
 * @Date 2023/8/23 22:35
 * @Version 1.0
 * @Description 脚本语言注册机
 */
public class LanguageDriverRegistry {

    private final Map<Class<?>,LanguageDriver> LANGUAGE_DRIVER_MAP = new HashMap<>();

    private Class<?> defaultDriverClass = null;

    public void register(Class<?> clazz){
        if(clazz == null){
            throw new IllegalArgumentException("null is not a valid Language Driver");
        }

        if(!LanguageDriver.class.isAssignableFrom(clazz)){
            throw new RuntimeException(clazz.getName() + "does not implement "+LanguageDriver.class.getName());
        }

        //如果没注册,就进行注册，这里应该是用来做缓存的，如果后面解析下一个类型的语句时，就需要重新注册脚本语言加载器了
        LanguageDriver languageDriver = LANGUAGE_DRIVER_MAP.get(clazz);
        if(languageDriver == null){
            try{
                //单例模式，一个class只有一个对应的LanguageDriver
                languageDriver = (LanguageDriver) clazz.newInstance();
                LANGUAGE_DRIVER_MAP.put(clazz,languageDriver);
            } catch (Exception e) {
                throw new RuntimeException("Failed to load language driver for "+clazz.getName(),e);
            }
        }

    }

    public LanguageDriver getDriver(Class<?> clazz){
        return LANGUAGE_DRIVER_MAP.get(clazz);
    }

    /**
     * 获取默认的语言驱动器
     * @return
     */
    public LanguageDriver getDefaultDriver(){
        return getDriver(getDefaultDriverClass());
    }

    public Class<?> getDefaultDriverClass() {
        return defaultDriverClass;
    }

    /**
     * 设置默认的语言驱动器，coniguration()有调用，默认的为XMLLanguageDriver
     * @param defaultDriverClass
     */
    public void setDefaultDriverClass(Class<?> defaultDriverClass){
        register(defaultDriverClass);
        this.defaultDriverClass = defaultDriverClass;
    }
}
