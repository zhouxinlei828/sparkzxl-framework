package com.sparksys.core.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Arrays;
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
     * @param obj 参数
     * @return Map<String, Object>
     */
    public static Map<String, Object> getKeyAndValue(Object obj) {
        Map<String, Object> map = Maps.newHashMap();
        List<Field> fields = getAllField(obj);
        handleField(obj, map, fields);
        return map;
    }

    private static void handleField(Object obj, Map<String, Object> map, List<Field> fields) {
        for (Field field : fields) {
            field.setAccessible(true);
            Object val;
            try {
                val = field.get(obj);
                map.putIfAbsent(field.getName(), val);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 单个对象的某个键的值
     *
     * @param obj 对象
     * @param key  属性值
     * @return Object
     */
    public static Object getValueByKey(Object obj, String key) {
        List<Field> fields = getAllField(obj);
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.getName().equalsIgnoreCase(key)) {
                    return field.get(obj);
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
     * @param object 对象
     * @return List<Map < String, Object>>
     */
    public static List<Map<String, Object>> getKeysAndValues(List<Object> object) {
        List<Map<String, Object>> list = Lists.newArrayList();
        for (Object obj : object) {
            List<Field> fields = getAllField(obj);
            Map<String, Object> listChild = Maps.newHashMap();
            handleField(obj, listChild, fields);
            list.add(listChild);
        }
        return list;
    }

    /**
     * 多个（列表）对象的某个键的值
     *
     * @param object 对象
     * @param key 属性key
     * @return List<Object>
     */
    public static List<Object> getValuesByKey(List<Object> object, String key) {
        List<Object> list = Lists.newArrayList();
        for (Object obj : object) {
            List<Field> fields = getAllField(obj);
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    if (field.getName().equalsIgnoreCase(key)) {
                        list.add(field.get(obj));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }


    public static Type getPropertyType(Object obj, String key) {
        List<Field> fields = getAllField(obj);
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.getName().equalsIgnoreCase(key)) {
                    return field.getGenericType();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static List<Field> getAllField(Object model) {
        Class clazz = model.getClass();
        List<Field> fields = Lists.newArrayList();
        while (clazz != null) {
            fields.addAll(Lists.newArrayList(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
}
