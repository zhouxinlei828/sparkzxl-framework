package com.sparksys.core.utils;

import cn.hutool.core.collection.CollUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.sparksys.core.support.SparkSysExceptionAssert;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

/**
 * description: Map 工具类
 *
 * @author: zhouxinlei
 * @date: 2020-07-19 09:40:56
 */
public class MapHelper {

    public static <K, V, M> ImmutableMap uniqueIndex(Iterable<V> values, Function<? super V, K> keyFunction, Function<? super V, M> valueFunction) {
        Iterator<V> iterator = values.iterator();
        Preconditions.checkNotNull(keyFunction);
        Preconditions.checkNotNull(valueFunction);
        ImmutableMap.Builder builder = ImmutableMap.builder();

        while (iterator.hasNext()) {
            V value = iterator.next();
            builder.put(keyFunction.apply(value), valueFunction.apply(value));
        }
        try {
            return builder.build();
        } catch (IllegalArgumentException var6) {
            SparkSysExceptionAssert.businessFail(var6.getMessage() + ".若要在键下索引多个值，请使用: Multimaps.index.");
        }
        return null;
    }

    public static <K, V> Map<V, K> inverse(Map<K, V> map) {
        if (CollUtil.isEmpty(map)) {
            return Collections.emptyMap();
        } else {
            HashBiMap<K, V> biMap = HashBiMap.create();
            map.forEach(biMap::forcePut);
            return biMap.inverse();
        }
    }

}
