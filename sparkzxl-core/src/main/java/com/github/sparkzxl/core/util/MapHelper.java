package com.github.sparkzxl.core.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * description: Map 类增强
 *
 * @author zhouxinlei
 * @since 2024-04-19 11:09:26
 */
public final class MapHelper {
    private MapHelper() {
    }

    /**
     * 移除map中空key或者value空值
     *
     * @param map map
     */
    public static void removeNullEntry(Map<String, Object> map) {
        removeNullKey(map);
        removeNullValue(map);
    }

    /**
     * 移除map的空key
     *
     * @param map map
     */
    public static void removeNullKey(Map<String, Object> map) {
        Set<String> set = map.keySet();
        for (Iterator<String> iterator = set.iterator(); iterator.hasNext(); ) {
            Object obj = iterator.next();
            remove(obj, iterator);
        }
    }

    /**
     * 移除map中的value空值
     *
     * @param map map
     */
    public static void removeNullValue(Map<String, Object> map) {
        Set<String> set = map.keySet();
        for (Iterator<String> iterator = set.iterator(); iterator.hasNext(); ) {
            Object obj = iterator.next();
            Object value = map.get(obj);
            remove(value, iterator);
        }
    }

    /**
     * 移除map中的空值
     * <p>
     * Iterator 是工作在一个独立的线程中，并且拥有一个 mutex 锁。 Iterator 被创建之后会建立一个指向原来对象的单链索引表，当原来的对象数量发生变化时，这个索引表的内容不会同步改变， 所以当索引指针往后移动的时候就找不到要迭代的对象，所以按照
     * fail-fast 原则 Iterator 会马上抛出 java.util.ConcurrentModificationException 异常。 所以 Iterator 在工作的时候是不允许被迭代的对象被改变的。 但你可以使用 Iterator 本身的方法
     * remove() 来删除对象， Iterator.remove() 方法会在删除当前迭代对象的同时维护索引的一致性。
     *
     * @param obj 对象
     * @param iterator 迭代器
     */
    private static void remove(Object obj, Iterator<String> iterator) {
        if (obj instanceof String) {
            String str = (String) obj;
            if (StringUtils.isBlank(str)) {
                iterator.remove();
            }

        } else if (obj instanceof Collection) {
            Collection col = (Collection) obj;
            if (col == null || col.isEmpty()) {
                iterator.remove();
            }

        } else if (obj instanceof Map) {
            Map temp = (Map) obj;
            if (temp == null || temp.isEmpty()) {
                iterator.remove();
            }

        } else if (obj instanceof Object[]) {
            Object[] array = (Object[]) obj;
            if (array == null || array.length <= 0) {
                iterator.remove();
            }
        } else {
            if (obj == null) {
                iterator.remove();
            }
        }
    }

    public static Map<String, Set<String>> putAll(Map<String, Set<String>>... items) {
        if (ArrayUtil.isEmpty(items)) {
            return Collections.emptyMap();
        }
        Map<String, Set<String>> map = new HashMap<>();
        for (Map<String, Set<String>> item : items) {
            item.forEach((k, v) -> {
                if (map.containsKey(k)) {
                    Set<String> list = map.get(k);

                    if (list == null) {
                        list = new HashSet<>();
                    }
                    list.addAll(v);
                } else {
                    map.put(k, new HashSet<>(v));
                }
            });
        }
        return map;
    }

    /**
     * 增强 guava 的 Maps.uniqueIndex方法
     * <p>
     * guava 的 Maps.uniqueIndex方法可以实现：
     * <br>
     * 将 list&lt;V&gt 转成 Map&lt;K , V&gt
     * K 需要自己指定， V不能指定
     * </p>
     * <p>
     * 本方法实现了：
     * <p>
     * 将 list&lt;V&gt 转成 Map&lt;K , M&gt
     * K 需要自己指定， M需要自己指定
     * <p>
     * 其中K不能重复，若重复，则会报错
     * </p>
     *
     * @param values        需要转换的集合 可以是任何实现了 Iterable 接口的集合(如List, Set, Collection)
     * @param keyFunction   转换后Map的键的转换方式
     * @param valueFunction 转换后Map的值的转换方式
     * @param <K>           转换后Map的键 类型
     * @param <V>           转换前Iterable的迭代类型
     * @param <M>           转换后Map的值 类型
     * @return 唯一的map
     */
    public static <K, V, M> ImmutableMap<K, M> uniqueIndex(Iterable<V> values, Function<? super V, K> keyFunction, Function<? super V, M> valueFunction) {
        Iterator<V> iterator = values.iterator();
        checkNotNull(keyFunction);
        checkNotNull(valueFunction);
        ImmutableMap.Builder<K, M> builder = ImmutableMap.builder();
        while (iterator.hasNext()) {
            V value = iterator.next();
            builder.put(keyFunction.apply(value), valueFunction.apply(value));
        }
        try {
            return builder.build();
        } catch (IllegalArgumentException duplicateKeys) {
            throw new IllegalArgumentException(
                    duplicateKeys.getMessage()
                            + ".若要在键下索引多个值，请使用: Multimaps.index.", duplicateKeys);
        }
    }

    /**
     * 一个key 对应多个值的map
     * 结构： key -> [value1, value2, ...]
     *
     * @param values        需要转换的集合 可以是任何实现了 Iterable 接口的集合(如List, Set, Collection)
     * @param keyFunction   转换后Map的键的转换方式
     * @param valueFunction 转换后Map的值的转换方式
     * @param <K>           转换后Map的键 类型
     * @param <V>           转换前Iterable的迭代类型
     * @param <M>           转换后Map的值 类型
     * @return 唯一的map
     */
    public static <K, V, M> Multimap<K, M> iterableToMultiMap(Iterable<V> values, Function<? super V, K> keyFunction, Function<? super V, M> valueFunction) {
        Iterator<V> iterator = values.iterator();
        checkNotNull(keyFunction);
        checkNotNull(valueFunction);

        Multimap<K, M> builder = ArrayListMultimap.create();
        while (iterator.hasNext()) {
            V value = iterator.next();
            builder.put(keyFunction.apply(value), valueFunction.apply(value));
        }
        try {
            return builder;
        } catch (IllegalArgumentException duplicateKeys) {
            throw new IllegalArgumentException(
                    duplicateKeys.getMessage()
                            + ".若要在键下索引多个值，请使用: Multimaps.index.", duplicateKeys);
        }
    }


    /**
     * 转换 Map 的 K 和 V
     *
     * @param map map
     * @return 反转的map
     */
    public static <K, V> Map<V, K> inverse(Map<K, V> map) {
        if (MapUtil.isEmpty(map)) {
            return Collections.emptyMap();
        }
        HashBiMap<K, V> biMap = HashBiMap.create();
        map.forEach(biMap::forcePut);
        return biMap.inverse();
    }

    /**
     * 计算map 初始容量
     *
     * @param size       已知数量
     * @param loadFactor 加载因子
     * @return map 初始容量
     */
    public static int initialCapacity(int size, float loadFactor) {
        return (int) (size / loadFactor + 1);
    }

    /**
     * 计算map 初始容量
     *
     * @param size 已知数量
     * @return map 初始容量
     */
    public static int initialCapacity(int size) {
        return initialCapacity(size, 0.75F);
    }

    /**
     * 按照分隔符切割list
     *
     * @param list      集合
     * @param function  转换器
     * @param separator 分隔符
     * @return 分割后的集合
     */
    public static <T> List<String> split(Collection<T> list, Function<? super T, ?> function, CharSequence separator) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.parallelStream().map(function).map(item -> StrUtil.splitToArray(String.valueOf(item), separator))
                .flatMap(Arrays::stream).filter(ObjectUtil::isNotEmpty).distinct().collect(Collectors.toList());
    }

    /**
     * 按照分隔符切割list
     *
     * @param list      集合
     * @param separator 分隔符
     * @return 分割后的集合
     */
    public static <T> List<String> split(Collection<String> list, CharSequence separator) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.parallelStream().map(item -> StrUtil.splitToArray(item, separator))
                .flatMap(Arrays::stream).filter(ObjectUtil::isNotEmpty).distinct().collect(Collectors.toList());
    }


    public static <E> List<E> asList(E... elements) {
        if (elements == null || elements.length == 0) {
            return Collections.emptyList();
        }
        // Avoid integer overflow when a large array is passed in
        int capacity = computeListCapacity(elements.length);
        ArrayList<E> list = new ArrayList<E>(capacity);
        Collections.addAll(list, elements);
        return list;
    }

    public static <E> Set<E> asSet(E... elements) {
        if (elements == null || elements.length == 0) {
            return Collections.emptySet();
        }
        LinkedHashSet<E> set = new LinkedHashSet<E>(elements.length * 4 / 3 + 1);
        Collections.addAll(set, elements);
        return set;
    }

    public static int computeListCapacity(int arraySize) {
        return (int) Math.min(5L + arraySize + (arraySize / 10), Integer.MAX_VALUE);
    }


    /**
     * 添加 多个List
     *
     * @param values 集合
     * @param <T>
     * @return
     */
    public static <T> List<T> addAll(List<T>... values) {
        return Stream.of(values).flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * 添加 多个List 并去重
     *
     * @param values 集合
     * @param <T>
     * @return
     */
    public static <T> List<T> addAllUnique(List<T>... values) {
        return Stream.of(values).flatMap(List::stream).distinct().collect(Collectors.toList());
    }

    /**
     * 添加 多个Set
     *
     * @param values 集合
     * @param <T>
     * @return
     */
    public static <T> Set<T> addAll(Set<T>... values) {
        return Stream.of(values).flatMap(Set::stream).collect(Collectors.toSet());
    }

    /**
     * 添加 多个List 并返回Set
     *
     * @param values 集合
     * @param <T>
     * @return
     */
    public static <T> Set<T> addAllListToSet(List<T>... values) {
        return Stream.of(values).flatMap(List::stream).collect(Collectors.toSet());
    }
}
