package com.tk.mybatis.reflection;

import com.tk.mybatis.reflection.invoker.GetFiledInvoker;
import com.tk.mybatis.reflection.invoker.Invoker;
import com.tk.mybatis.reflection.invoker.MethodInvoker;
import com.tk.mybatis.reflection.invoker.SetFieldInvoker;
import com.tk.mybatis.reflection.property.PropertyNamer;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author liuht
 * @Date 2023/8/14 19:05
 * @Version 1.0
 * @Description 反射器解耦对象
 */
public class Reflector {
    private static boolean classCacheEnable = true;

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static final Map<Class<?> , Reflector> REFLECTOR_MAP = new ConcurrentHashMap<>();

    private Class<?> type;

    /**
     * get属性列表
     */
    private String[] readablePropertyNames = EMPTY_STRING_ARRAY;

    /**
     * set属性列表
     */
    private String[] writeablePropertynames = EMPTY_STRING_ARRAY;

    //set方法反射调用集合
    private Map<String, Invoker> setMethods = new HashMap<>();

    //get方法反射调用集合
    private Map<String, Invoker> getMethods = new HashMap<>();

    // set方法类型列表
    private Map<String, Class<?>> setTypes = new HashMap<>();

    //get方法参数类型列表
    private Map<String, Class<?>> getTypes = new HashMap<>();

    //构造函数
    private Constructor<?> defaultConstrutor;

    private Map<String,String> caseInsensitivePropertyMap = new HashMap<>();

    public Reflector(Class<?> clazz){
        this.type = clazz;

        //加入构造方法
        addConstractor(clazz);
        //加入get方法
        addGetMethods(clazz);
        //加入set方法
        addSetMethods(clazz);
        //加入字段
        addFields(clazz);

        readablePropertyNames = getMethods.keySet().toArray(new String[getMethods.keySet().size()]);
        writeablePropertynames = setMethods.keySet().toArray(new String[setMethods.keySet().size()]);

        for(String name : readablePropertyNames){
            caseInsensitivePropertyMap.put(name.toUpperCase(Locale.ENGLISH),name);
        }

        for(String name : writeablePropertynames){
            caseInsensitivePropertyMap.put(name.toUpperCase(Locale.ENGLISH),name);
        }
    }

    public static boolean isClassCacheEnable() {
        return classCacheEnable;
    }

    public static void setClassCacheEnbale(boolean classCacheEnable) {
        Reflector.classCacheEnable = classCacheEnable;
    }

    /**
     * 添加属性相关的方法，比如get、set方法
     *
     * @param clazz
     */
    private void addFields(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for(Field field : declaredFields){
            if(canAccessPrivateMethods()){
                try{
                    field.setAccessible(true);
                }catch (Exception ingore){
                    //忽略报错
                }
            }

            if(field.isAccessible()){
                if(!setMethods.containsKey(field.getName())){
                    int modifiers = field.getModifiers();
                    if(!(Modifier.isFinal(modifiers)||Modifier.isStatic(modifiers))){
                        addSetFiled(field);
                    }
                }

                if(!getMethods.containsKey(field.getName())){
                    addGetField(field);
                }
            }
        }
        if(clazz.getSuperclass() != null){
            addFields(clazz.getSuperclass());
        }
    }

    private void addGetField(Field field) {
        if(isValidPropertyName(field.getName())){
            getMethods.put(field.getName(), new GetFiledInvoker(field));
            getTypes.put(field.getName(), field.getType());
        }
    }

    private void addSetFiled(Field field) {
        if(isValidPropertyName(field.getName())){
            setMethods.put(field.getName(), new SetFieldInvoker(field));
            setTypes.put(field.getName(), field.getType());
        }
    }

    /**
     * 创建set方法反射调用类
     * @param clazz
     */
    private void addSetMethods(Class<?> clazz) {
        Method[] methods = getClassMethods(clazz);
        HashMap<String, List<Method>> conflictSetters = new HashMap<>();
        for(Method method : methods){
            String name = method.getName();
            if(name.startsWith("set") && name.length() > 3){
                if(method.getParameterTypes().length == 1){
                     name = PropertyNamer.methodToProperty(name);
                     conflictSetters(conflictSetters,name,method);
                }
            }
        }
        resloveSetterConflicts(conflictSetters);
    }

    /**
     *
     * @param conflictSetters
     */
    private void resloveSetterConflicts(HashMap<String, List<Method>> conflictSetters) {
        for (String name :conflictSetters.keySet()){
            List<Method> methods = conflictSetters.get(name);
            Method firstMethod = methods.get(0);
            if(methods.size() == 1){
                addSetterMethod(name,firstMethod);
            }else {
                Class<?> exceptType = getTypes.get(name);
                if(exceptType == null){
                    throw new RuntimeException("Illegal overloaded setter method with ambiguous type for property "
                            + name + " in class " + firstMethod.getDeclaringClass() + ".  This breaks the JavaBeans " +
                            "specification and can cause unpredicatble results.");
                }else {
                    //对set方法集合进行遍历，找出参数类型和get方法返回类型一致的方法
                    Method setter = null;
                    Iterator<Method> iterator = methods.iterator();
                    if(iterator.hasNext()){
                        Method method = iterator.next();
                        Class<?>[] parameterTypes = method.getParameterTypes();
                        if(parameterTypes.length == 1 && exceptType.equals(parameterTypes[0])){
                            setter = method;
                            break;
                        }
                    }
                    if(setter == null){
                        throw new RuntimeException("Illegal overloaded setter method with ambiguous type for property "
                                + name + " in class " + firstMethod.getDeclaringClass() + ".  This breaks the JavaBeans " +
                                "specification and can cause unpredicatble results.");
                    }
                    addSetterMethod(name,setter);
                }
            }
        }
    }

    private void addSetterMethod(String name, Method firstMethod) {
        if(isValidPropertyName(name)){
            setMethods.put(name,new MethodInvoker(firstMethod));
            setTypes.put(name,firstMethod.getParameterTypes()[0]);
        }
    }

    private void conflictSetters(HashMap<String, List<Method>> conflictSetters, String name, Method method) {
        List<Method> list = conflictSetters.computeIfAbsent(name, k -> new ArrayList<>());
        list.add(method);
    }

    /**
     * 创建get反射调用类
     * @param clazz
     */
    private void addGetMethods(Class<?> clazz) {
        Method[] methods = getClassMethods(clazz);
        Map<String, List<Method>> conflictGetters = new HashMap<>();
        for(Method method : methods){
            String name = method.getName();
            if(name.startsWith("get") && name.length() > 3){
                if(method.getParameterTypes().length == 0){
                    name = PropertyNamer.methodToProperty(name);
                    conflictGetters(conflictGetters,name,method);
                }
            }else if(name.startsWith("is") && name.length() > 2){
                if(method.getParameterTypes().length == 0){
                    name = PropertyNamer.methodToProperty(name);
                    conflictGetters(conflictGetters,name,method);
                }
            }
        }

        resolveGetterConflicts(conflictGetters);
    }

    /**
     * 解决具有相同属性名但返回类型不同的多个"getter"方法的冲突，并决定使用哪个getter方法作为最终的getter
     * @param conflictGetters
     */
    private void resolveGetterConflicts(Map<String, List<Method>> conflictGetters) {
        Set<String> methodNames = conflictGetters.keySet();

        for(String name : methodNames){
            List<Method> getters = conflictGetters.get(name);
            Iterator<Method> iterator = getters.iterator();
            Method firstMethod = iterator.next();
            if(getters.size() == 1){
                addGetMethod(name,firstMethod);
            }else {
                Method getter = firstMethod;
                Class<?> returnType = getter.getReturnType();
                while(iterator.hasNext()){
                    Method method = iterator.next();
                    Class<?> methodReturnType = method.getReturnType();
                    if(methodReturnType.equals(returnType)){
                        //不可能有两个方法名相同，返回类型也相同的方法
                        throw new RuntimeException("Illegal overloaded getter method with ambiguous type for property "
                                + name + " in class " + firstMethod.getDeclaringClass()
                                + ".  This breaks the JavaBeans " + "specification and can cause unpredicatble results.");
                    }else if(methodReturnType.isAssignableFrom(returnType)){
                        //methodReturnType 是 returnType的子类 跳过这个循环
                    }else if(returnType.isAssignableFrom(methodReturnType)){
                        //returnType是methodReturnType的子类,需要将返回类型更新成methodReturnType
                        getter = method;
                        returnType = methodReturnType;
                    }else {
                        throw new RuntimeException("Illegal overloaded getter method with ambiguous type for property "
                                + name + " in class " + firstMethod.getDeclaringClass()
                                + ".  This breaks the JavaBeans " + "specification and can cause unpredicatble results.");
                    }
                }
                addGetMethod(name,getter);
            }
        }
    }

    /**
     * 将get放射方法存入map
     * @param name
     * @param firstMethod
     */
    private void addGetMethod(String name, Method firstMethod) {
        if(isValidPropertyName(name)){
            getMethods.put(name,new MethodInvoker(firstMethod));
            getTypes.put(name,firstMethod.getReturnType());
        }
    }

    /**
     * 检查方法名是不是特殊类方法
     * @param name
     * @return
     */
    private boolean isValidPropertyName(String name) {
        return !(name.startsWith("$") || "serialVersionUID".equals(name) || "class".equals(name));
    }

    /**
     * 存储get方法
     * 选择将"getter"方法存储在 List<Method> 对象中，而不是直接存储在 Map 中，是因为一个属性可能有多个"getter"方法。
     * 多个"getter"方法可能是由于继承、重载或接口的实现等多种原因导致的
     * @param conflictGetters
     * @param name
     * @param method
     */
    private void conflictGetters(Map<String, List<Method>> conflictGetters, String name, Method method) {
        List<Method> list = conflictGetters.computeIfAbsent(name, k -> new ArrayList<>());
        list.add(method);
    }

    /**
     * 获取类的所有方法
     * @param clazz
     * @return
     */
    private Method[] getClassMethods(Class<?> clazz) {
        Map<String,Method> uniqueMethods = new HashMap<>();

        Class<?> currentClass = clazz;
        while(currentClass != null){
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());

            //这个类可能只是一个子类，有继承的弗雷或者实现的接口，所以还需要遍历它的父类
            Class<?>[] interfaces = currentClass.getInterfaces();
            for(Class<?> intf : interfaces){
                addUniqueMethods(uniqueMethods, intf.getMethods());
            }

            currentClass = currentClass.getSuperclass();
        }
        Collection<Method> methods = uniqueMethods.values();
        return methods.toArray(new Method[methods.size()]);
    }

    /**
     * 添加唯一的方法到unqiqueMethods中
     * @param uniqueMethods
     * @param declaredMethods
     */
    private void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] declaredMethods) {
        for(Method method : declaredMethods){
            /**
             * 判断方法是否是桥接方法
             * 桥接方法是用于解决泛型类型擦除引起的问题
             * 作用是在子类中生成一个方法，确保子类继承的方法语弗雷德方法签名一致，维护编译时和运行时的类型安全性
             * 总而言之 桥接方法其实不是真正的我们自己定义的方法
             */
            if(!method.isBridge()){
                //获取签名
                String signature = getSignature(method);

                if(!uniqueMethods.containsKey(signature)){
                    try{
                        if(canAccessPrivateMethods()){
                            method.setAccessible(true);
                        }
                    }catch (Exception ingore){

                    }
                    uniqueMethods.put(signature,method);
                }
            }
        }
    }


    /**
     * 获取方法签名
     * @param method
     * @return
     */
    private String getSignature(Method method){
        StringBuilder stringBuilder = new StringBuilder();
        Class<?> returnType = method.getReturnType();

        //添加返回类型到签名中
        if(returnType != null){
            stringBuilder.append(returnType.getName()).append("#");
        }

        //添加方法到签名
        stringBuilder.append(method.getName());

        //添加参数类型到签名
        Class<?>[] parameterTypes = method.getParameterTypes();
        for(int i = 0;i< parameterTypes.length;i++){
            if(i == 0){
                stringBuilder.append(":");
            }else {
                stringBuilder.append(",");
            }
            stringBuilder.append(parameterTypes[i].getName());
        }

        return stringBuilder.toString();

    }

    /**
     * 加入构造方法
     * @param clazz
     */
    private void addConstractor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();

        for(Constructor constructor : constructors){
            if(constructor.getParameterCount() == 0){
                if(canAccessPrivateMethods()){
                    try{
                        constructor.setAccessible(true);
                    }catch (Exception ingore){

                    }
                }
            }

            if(constructor.isAccessible()){
                this.defaultConstrutor = constructor;
            }
        }
    }

    /**
     * 检查当前方法是否拥有反射访问私有方法的权限
     * @return
     */
    private boolean canAccessPrivateMethods() {
        try{
            //SecurityManager提供了一种管理和执行应用程序中代码执行操作的安全策略的方式
            SecurityManager securityManager = System.getSecurityManager();
            if(null != securityManager){
                //所检查的权限是使用 ReflectPermission 类和字符串 "suppressAccessChecks" 创建的
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        }catch (Exception e){
            return false;
        }

        return true;
    }


    /**
     * 获取某个类的反射器
     * @param clazz
     * @return
     */
    public static Reflector forClass(Class<?> clazz){
        Reflector reflector;
        if(classCacheEnable){
            reflector = REFLECTOR_MAP.get(clazz);
            if(reflector == null){
                reflector = new Reflector(clazz);
                REFLECTOR_MAP.put(clazz,reflector);
            }
        }else {
            reflector = new Reflector(clazz);
        }

        return reflector;
    }


    public String[] getGetablePropertyNames() {
        return readablePropertyNames;
    }

    public String[] getSetablePropertyNames(){
        return writeablePropertynames;
    }

    public Invoker getGetInvoker(String name) {
        Invoker method = getMethods.get(name);
        if(method == null){
            throw new RuntimeException("There is no getter for property named '" + name + "' in '" + type + "'");
        }
        return method;
    }

    public Invoker getSetInvoker(String name) {
        Invoker method = setMethods.get(name);
        if(method == null){
            throw new RuntimeException("There is no setter for property named '" + name + "' in '" + type + "'");
        }
        return method;
    }

    public Class<?> getGetterType(String name) {
        Class<?> clazz = getTypes.get(name);
        if(clazz == null){
            throw new RuntimeException("There is no getter for property named '" + name + "' in '" + type + "'");
        }
        return type;
    }

    public String findPropertyname(String name) {
        return caseInsensitivePropertyMap.get(name.toUpperCase(Locale.ENGLISH));
    }

    public boolean hasSetter(String name) {
        return setMethods.containsKey(name);
    }

    public boolean hasGetter(String name){
        return getMethods.containsKey(name);
    }

    public Class<?> getSetterType(String name) {
        Class<?> clazz = setTypes.get(name);
        if(clazz == null){
            throw new RuntimeException("There is no setter for property named '" + name + "' in '" + type + "'");
        }
        return clazz;
    }
}
