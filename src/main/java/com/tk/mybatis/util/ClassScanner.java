package com.tk.mybatis.util;

import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author liuht
 * @Date 2023/7/29 21:13
 * @Version 1.0
 * @Description 描述
 */
public class ClassScanner {

    /**
     * 扫描包下面的类
     * @param packagePath
     * @return
     */
    public static Set<Class<?>> scanPackge(String packagePath) {
        Set<Class<?>> classes = new HashSet<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String packageName;
        if(!checkPackagePath(packagePath)){
            throw new RuntimeException("packagePath "+packagePath+" is not compliance");
        }else {
            packageName = packagePath.trim().replace(".","/");
        };

        try {
            Enumeration<URL> resources = classLoader.getResources(packageName);
            while(resources.hasMoreElements()){
                URL resource = resources.nextElement();
                File file = new File(resource.getFile());
                if(file.isDirectory()){
                    scanClassesForDirectory(packagePath,file,classes);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 扫描包路径
     * @param packagePath
     * @param directory
     * @param classes
     */
    private static void scanClassesForDirectory(String packagePath, File directory, Set<Class<?>> classes) {
        File[] files = directory.listFiles();
        if(files == null){
            return;
        }

        for(File file : files){
            if(file.isDirectory()){
                //文件下面还有文件夹 采用递归解析的方式进行逐层遍历
                scanClassesForDirectory(packagePath+"."+file.getName(),file, classes);
            }else if(file.getName().endsWith(".class")){
                String className = packagePath + "." + file.getName().substring(0,file.getName().length()-6);
                try {
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 校验并格式化包路径
     * @param packagePath
     */
    private static boolean checkPackagePath(String packagePath) {
        if(packagePath == null || Strings.isEmpty(packagePath)){
            return false;
        }
        return true;
    }
}
