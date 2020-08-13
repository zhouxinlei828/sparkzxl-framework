package com.sparksys.core.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sparksys.core.entity.AuthUserInfo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * description: 反射对象工具类
 *
 * @author: zhouxinlei
 * @date: 2020-08-13 11:42:30
 */
public class ReflexObjectUtils {

    /**
     * 单个对象的所有键值
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> getKeyAndValue(Object obj) {
        Map<String, Object> map = Maps.newHashMap();
        // 得到类对象
        Class userCla = obj.getClass();
        Field[] fs = userCla.getDeclaredFields();
        handleField(obj, map, fs);
        System.out.println("单个对象的所有键值==反射==" + map.toString());
        return map;
    }

    private static void handleField(Object obj, Map<String, Object> map, Field[] fs) {
        for (Field f : fs) {
            f.setAccessible(true);
            Object val;
            try {
                val = f.get(obj);
                map.putIfAbsent(f.getName(), val);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 单个对象的某个键的值
     *
     * @param obj
     * @param key
     * @return
     */
    public static Object getValueByKey(Object obj, String key) {
        // 得到类对象
        Class userCla = obj.getClass();
        /* 得到类中的所有属性集合 */
        Field[] fs = userCla.getDeclaredFields();
        for (Field f : fs) {
            f.setAccessible(true);
            try {
                if (f.getName().endsWith(key)) {
                    System.out.println("单个对象的某个键的值==反射==" + f.get(obj));
                    return f.get(obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 没有查到时返回空字符串
        return "";
    }

    /**
     * 多个（列表）对象的所有键值
     *
     * @param object
     * @return
     */
    public static List<Map<String, Object>> getKeysAndValues(List<Object> object) {
        List<Map<String, Object>> list = Lists.newArrayList();
        for (Object obj : object) {
            Class userCla;
            // 得到类对象
            userCla = obj.getClass();
            /* 得到类中的所有属性集合 */
            Field[] fs = userCla.getDeclaredFields();
            Map<String, Object> listChild = Maps.newHashMap();
            handleField(obj, listChild, fs);
            list.add(listChild);
        }
        System.out.println("多个（列表）对象的所有键值====" + list.toString());
        return list;
    }

    /**
     * 多个（列表）对象的某个键的值
     *
     * @param object
     * @param key
     * @return
     */
    public static List<Object> getValuesByKey(List<Object> object, String key) {
        List<Object> list = Lists.newArrayList();
        for (Object obj : object) {
            // 得到类对象
            Class userCla = obj.getClass();
            /* 得到类中的所有属性集合 */
            Field[] fs = userCla.getDeclaredFields();
            for (Field f : fs) {
                f.setAccessible(true);
                try {
                    if (f.getName().endsWith(key)) {
                        list.add(f.get(obj));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("多个（列表）对象的某个键的值列表====" + list.toString());
        return list;
    }


    public static Class getPropertyType(Object obj, String key) {
        // 得到类对象
        Class userCla = obj.getClass();
        /* 得到类中的所有属性集合 */
        Field[] fs = userCla.getDeclaredFields();
        for (Field f : fs) {
            f.setAccessible(true);
            try {
                if (f.getName().endsWith(key)) {
                    System.out.println("单个对象的某个键的值==反射==" + f.get(obj));
                    return f.get(obj).getClass();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 没有查到时返回空字符串
        return null;
    }

    public static void main(String[] args) {
        AuthUserInfo authUserInfo = new AuthUserInfo();
        authUserInfo.setName("1212");
        System.out.println(getPropertyType(authUserInfo, "name").getName());
    }
}
