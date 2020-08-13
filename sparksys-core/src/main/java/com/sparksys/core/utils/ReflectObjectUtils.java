package com.sparksys.core.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * description: 反射对象工具类
 *
 * @author: zhouxinlei
 * @date: 2020-08-13 11:42:30
 */
public class ReflectObjectUtils {

    /**
     * 单个对象的所有键值
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> getKeyAndValue(Object obj) {
        Map<String, Object> map = Maps.newHashMap();
        Class userCla = obj.getClass();
        Field[] fs = userCla.getDeclaredFields();
        handleField(obj, map, fs);
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
        Class userCla = obj.getClass();
        Field[] fs = userCla.getDeclaredFields();
        for (Field f : fs) {
            f.setAccessible(true);
            try {
                if (f.getName().equalsIgnoreCase(key)) {
                    return f.get(obj);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            userCla = obj.getClass();
            Field[] fs = userCla.getDeclaredFields();
            Map<String, Object> listChild = Maps.newHashMap();
            handleField(obj, listChild, fs);
            list.add(listChild);
        }
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
            Class userCla = obj.getClass();
            Field[] fs = userCla.getDeclaredFields();
            for (Field f : fs) {
                f.setAccessible(true);
                try {
                    if (f.getName().equalsIgnoreCase(key)) {
                        list.add(f.get(obj));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }


    public static Type getPropertyType(Object obj, String key) {
        Class userCla = obj.getClass();
        Field[] fs = userCla.getDeclaredFields();
        for (Field f : fs) {
            f.setAccessible(true);
            try {
                if (f.getName().equalsIgnoreCase(key)) {
                    return f.getGenericType();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*public static void main(String[] args) {
        AuthUserInfo authUserInfo = new AuthUserInfo();
        authUserInfo.setId(123123123L);
        Object o = authUserInfo;
        System.out.println(getPropertyType(o, "id").getTypeName().equals(Long.class.getName()));
    }*/
}
