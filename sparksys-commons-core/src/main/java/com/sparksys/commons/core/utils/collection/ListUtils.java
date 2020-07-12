package com.sparksys.commons.core.utils.collection;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * description: ListUtils工具类
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:49:53
 */
@Slf4j
public class ListUtils extends ListUtil {

    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    /**
     * 判断list是否为空
     *
     * @param list
     * @return boolean
     * @author zhouxinlei
     * @date 2019-11-20 11:22:32
     */
    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 判断list是否不为空
     *
     * @param list
     * @return boolean
     * @author zhouxinlei
     * @date 2019-11-20 11:22:32
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
     * 获取指定区间的集合
     *
     * @param list
     * @param start
     * @param end
     * @return List<T>
     * @author zhouxinlei
     * @date 2019-11-20 10:36:15
     */
    public static <T> List<T> getBetweenElement(List<T> list, Integer start, Integer end) {
        if (isEmpty(list)) {
            return emptyList();
        }
        ArrayList<T> newList = new ArrayList<T>(end - start);
        for (int i = start; i < end; i++) {
            if (i > list.size() - 1) {
                break;
            }
            newList.add(list.get(i));
        }
        return newList;
    }

    /**
     * set转list
     *
     * @param set
     * @return List<T>
     * @author zhouxinlei
     * @date 2019-11-20 10:36:48
     */
    public static <T> List<T> setToList(Set<T> set) {
        return new ArrayList<>(set);
    }

    /**
     * list转set
     *
     * @param list
     * @return Set<T>
     * @author zhouxinlei
     * @date 2019-11-20 10:37:04
     */
    public static <T> Set<T> listToSet(List<T> list) {
        return new HashSet<>(list);
    }

    /**
     * 数组转list
     *
     * @param ids
     * @return List<T>
     * @author zhouxinlei
     * @date 2019-11-20 10:37:22
     */
    public static <T> List<T> arrayToList(T[] ids) {
        return Arrays.stream(ids).collect(Collectors.toList());
    }

    /**
     * 数组转set
     *
     * @param ids
     * @return List<T>
     * @author zhouxinlei
     * @date 2019-11-20 10:37:22
     */
    public static <T> Set<T> arrayToSet(T[] ids) {
        return Arrays.stream(ids).collect(Collectors.toSet());

    }

    /**
     * list转String
     *
     * @param list
     * @return List<T>
     * @author zhouxinlei
     * @date 2019-11-20 10:37:22
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
     * @param data
     * @return List<T>
     * @author zhouxinlei
     * @date 2019-11-20 10:37:22
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

    public static void main(String[] args) {
        System.out.println(ListUtils.stringToLongList("1,2,3,4,5"));
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
     * 过滤重复id
     *
     * @param keyExtractor
     * @return Predicate<T>
     * @author zhouxinlei
     * @date 2019-11-20 11:21:13
     */
    public static <T> Predicate<T> distinctById(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    /**
     * 差集（扣除）
     *
     * @param a
     * @param b
     * @return List<T>
     * @author zhouxinlei
     * @date 2019-09-29 22:45:18
     */
    public static <T> List<T> intersection(List<T> a, List<T> b) {
        return a.stream().filter(x -> !b.contains(x)).collect(Collectors.toList());
    }

    /**
     * 取并集
     *
     * @param a
     * @param b
     * @return List<T>
     * @author zhouxinlei
     * @date 2019-12-27 09:40:48
     */
    public static <T> List<T> unionList(List<T> a, List<T> b) {
        return new ArrayList<>(CollectionUtils.union(a, b));
    }

    public static <T> void copyList(Object obj, List<T> list2, Class<T> classObj) {
        if ((!Objects.isNull(obj)) && (!Objects.isNull(list2))) {
            List list1 = (List) obj;
            list1.forEach(item -> {
                try {
                    T data = classObj.newInstance();
                    BeanUtils.copyProperties(item, data);
                    list2.add(data);
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            });
        }
    }
}
