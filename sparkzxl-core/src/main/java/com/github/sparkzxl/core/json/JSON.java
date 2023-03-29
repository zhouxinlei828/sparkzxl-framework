package com.github.sparkzxl.core.json;

import cn.hutool.core.lang.TypeReference;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * description: JSON
 *
 * @author zhouxinlei
 * @since 2022-12-30 14:32:35
 */
public interface JSON {

    /**
     * json类型
     *
     * @return String
     */
    String named();

    /**
     * 是否支持
     *
     * @return boolean
     */
    boolean isSupport();

    /**
     * 转换为json字符串
     *
     * @param val 对象val
     * @return String
     */
    String toJson(Object val);

    /**
     * 转换为json美化过的字符串
     *
     * @param val 对象val
     * @return String
     */
    String toJsonPretty(Object val);

    /**
     * 将json字符串转换为实体对象
     *
     * @param json json字符
     * @param type 类型
     * @param <T>  泛型
     * @return T
     */
    <T> T toJavaObject(String json, Type type);

    /**
     * 将json字符串转换为实体对象
     *
     * @param json          json字符串
     * @param typeReference 泛型类型
     * @param <T>           泛型
     * @return T
     */
    <T> T toJavaObject(String json, TypeReference<T> typeReference);

    /**
     * 将对象转换为实体对象
     *
     * @param val  对象
     * @param type 类型
     * @param <T>  泛型
     * @return T
     */
    <T> T toJavaObject(Object val, Type type);

    /**
     * 将json字符串转换为list实体对象
     *
     * @param json  json字符串
     * @param clazz 类型
     * @param <T>   泛型
     * @return T
     */
    <T> List<T> toJavaList(final String json, Class<T> clazz);

    /**
     * 获取list对象
     *
     * @param val Map 对象
     * @param key key值
     * @return List<?>
     */
    List<?> getList(Map<String, ?> val, String key);

    /**
     * 将json字符串转换为Map对象
     *
     * @param json json字符串
     * @return Map<String, Object>
     */
    Map<String, Object> toMap(String json);

    /**
     * 将实体对象转换为Map对象
     *
     * @param val 实体对象
     * @return Map<String, Object>
     */
    Map<String, Object> toMap(Object val);

    /**
     * 将json字符串转换为Map对象
     *
     * @param json  json字符串
     * @param clazz 类型
     * @return Map<String, T>
     */
    <T> Map<String, T> toMap(final String json, final Class<T> clazz);

    /**
     * 将实体对象转换为Map对象
     *
     * @param val   实体对象
     * @param clazz 类型
     * @return Map<String, T>
     */
    <T, E> Map<String, T> toMap(E val, Class<T> clazz);
}
