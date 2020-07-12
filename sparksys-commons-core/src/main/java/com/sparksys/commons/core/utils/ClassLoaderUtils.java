package com.sparksys.commons.core.utils;

import cn.hutool.core.util.ClassLoaderUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * description: 类加载工具类
 *
 * @author: zhouxinlei
 * @date: 2020-07-08 11:22:32
 */
public class ClassLoaderUtils extends ClassLoaderUtil {

    private static final Set<Class> PRIMITIVE_SET = new HashSet<>();

    static {
        PRIMITIVE_SET.add(Integer.class);
        PRIMITIVE_SET.add(Long.class);
        PRIMITIVE_SET.add(Float.class);
        PRIMITIVE_SET.add(Byte.class);
        PRIMITIVE_SET.add(Short.class);
        PRIMITIVE_SET.add(Double.class);
        PRIMITIVE_SET.add(Character.class);
        PRIMITIVE_SET.add(Boolean.class);
    }

    /**
     * 实例化一个对象(只检测默认构造函数，其它不管）
     *
     * @param clazz 对象类
     * @param <T>   对象具体类
     * @return 对象实例
     * @throws Exception 没有找到方法，或者无法处理，或者初始化方法异常等
     */
    public static <T> T newInstance(Class<T> clazz) throws Exception {
        if (PRIMITIVE_SET.contains(clazz)) {
            return null;
        }
        if (clazz.isMemberClass() && !Modifier.isStatic(clazz.getModifiers())) {
            Constructor[] constructorList = clazz.getDeclaredConstructors();
            Constructor defaultConstructor = null;
            for (Constructor con : constructorList) {
                if (con.getParameterTypes().length == 1) {
                    defaultConstructor = con;
                    break;
                }
            }
            if (defaultConstructor != null) {
                if (defaultConstructor.isAccessible()) {
                    return (T) defaultConstructor.newInstance(new Object[]{null});
                } else {
                    try {
                        defaultConstructor.setAccessible(true);
                        return (T) defaultConstructor.newInstance(new Object[]{null});
                    } finally {
                        defaultConstructor.setAccessible(false);
                    }
                }
            } else {
                throw new Exception("The " + clazz.getCanonicalName() + " has no default constructor!");
            }
        }
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            if (constructor.isAccessible()) {
                throw new Exception("The " + clazz.getCanonicalName() + " has no default constructor!", e);
            } else {
                try {
                    constructor.setAccessible(true);
                    return constructor.newInstance();
                } finally {
                    constructor.setAccessible(false);
                }
            }
        }
    }

    public static Class<?>[] getClazzByArgs(Object[] args) {
        Class<?>[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ArrayList) {
                parameterTypes[i] = List.class;
                continue;
            }
            if (args[i] instanceof LinkedList) {
                parameterTypes[i] = List.class;
                continue;
            }
            if (args[i] instanceof HashMap) {
                parameterTypes[i] = Map.class;
                continue;
            }
            if (args[i] instanceof Long) {
                parameterTypes[i] = long.class;
                continue;
            }
            if (args[i] instanceof Double) {
                parameterTypes[i] = double.class;
                continue;
            }
            if (args[i] instanceof TimeUnit) {
                parameterTypes[i] = TimeUnit.class;
                continue;
            }
            parameterTypes[i] = args[i].getClass();
        }
        return parameterTypes;
    }

    public Method getMethod(Class<?> classType, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        return classType.getMethod(methodName, parameterTypes);
    }

}
