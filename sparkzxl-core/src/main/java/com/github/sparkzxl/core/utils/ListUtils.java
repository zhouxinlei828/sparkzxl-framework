package com.github.sparkzxl.core.utils;

import cn.hutool.core.convert.Convert;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * description: 集合工具类
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:49:53
 */
@Slf4j
public class ListUtils {

    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    /**
     * 判断list是否为空
     *
     * @param list list集合
     * @return boolean
     */
    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 判断list是否不为空
     *
     * @param list list集合
     * @return boolean
     */
    public static <T> boolean isNotEmpty(List<T> list) {
        return !isEmpty(list);
    }


    public static <T> List<T> single(T value) {
        List<T> list = new ArrayList<>(1);
        list.add(value);
        return list;
    }

    /**
     * set转list
     *
     * @param set set集合
     * @return List<T>
     */
    public static <T> List<T> setToList(Set<T> set) {
        return new ArrayList<>(set);
    }

    /**
     * list转set
     *
     * @param list list集合
     * @return Set<T>
     */
    public static <T> Set<T> listToSet(List<T> list) {
        return new HashSet<>(list);
    }

    /**
     * 数组转list
     *
     * @param ts array数组
     * @return List<T>
     */
    public static <T> List<T> arrayToList(T[] ts) {
        return Arrays.stream(ts).collect(Collectors.toList());
    }

    /**
     * 数组转set
     *
     * @param ts array数组
     * @return List<T>
     */
    public static <T> Set<T> arrayToSet(T[] ts) {
        return Arrays.stream(ts).collect(Collectors.toSet());

    }

    /**
     * list转String
     *
     * @param list list
     * @return List<T>
     */
    public static String listToString(List<String> list) {
        String str = "";
        if (isNotEmpty(list)) {
            str = String.join(",", list);
        }
        return str;
    }

    /**
     * String转list
     *
     * @param data 字符串
     * @return List<T>
     */
    public static List<String> stringToList(String data) {
        if (StringUtils.isNotEmpty(data)) {
            String[] str = data.split(",");
            return Arrays.asList(str);
        } else {
            return emptyList();
        }
    }

    public static List<Long> stringToLongList(String data) {
        if (StringUtils.isNotEmpty(data)) {
            String[] str = data.split(",");
            Long[] strArrNum = Convert.toLongArray(str);
            return Arrays.asList(strArrNum);
        } else {
            return emptyList();
        }
    }

    public static List<Integer> stringToIntegerList(String data) {
        if (StringUtils.isNotEmpty(data)) {
            String[] str = data.split(",");
            Integer[] strArrNum = Convert.toIntArray(str);
            return Arrays.asList(strArrNum);
        } else {
            return emptyList();
        }
    }

    public static String[] stringToArray(String data) {
        if (StringUtils.isNotEmpty(data)) {
            return data.split(",");
        } else {
            return new String[0];
        }
    }


    public static String[] stringToArray(List<String> data) {
        if (isNotEmpty(data)) {
            return data.stream().map(String::valueOf).toArray(String[]::new);
        } else {
            return new String[0];
        }
    }

    /**
     * 差集（扣除）
     *
     * @param a 参数a
     * @param b 参数b
     * @return List<T>
     */
    public static <T> List<T> intersection(List<T> a, List<T> b) {
        return a.stream().filter(x -> !b.contains(x)).collect(Collectors.toList());
    }

    /**
     * 取并集
     *
     * @param a 参数a
     * @param b 参数b
     * @return List<T>
     */
    public static <T> List<T> unionList(List<T> a, List<T> b) {
        return new ArrayList<>(CollectionUtils.union(a, b));
    }
}
