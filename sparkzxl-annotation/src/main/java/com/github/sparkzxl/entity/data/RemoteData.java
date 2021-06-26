package com.github.sparkzxl.entity.data;

import cn.hutool.core.util.ObjectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * description: 远程数据对象
 * <K> ID或者code 等唯一键
 * <D> 根据key 远程查询出的数据
 *
 * @author zhouxinlei
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemoteData<K, D> implements Serializable {

    private K key;
    private D data;


    public RemoteData(K key) {
        this.key = key;
    }

    /**
     * 获取对象的 主键key
     *
     * @param remoteData data
     * @return K
     */
    public static <K, D> K getKey(RemoteData<K, D> remoteData) {
        return remoteData != null ? remoteData.getKey() : null;
    }

    public static <K, D> K getKey(RemoteData<K, D> remoteData, K def) {
        return remoteData != null && ObjectUtil.isNotEmpty(remoteData.getKey()) ? remoteData.getKey() : def;
    }

    /**
     * 获取对象的 data
     *
     * @param remoteData data
     * @return D
     */
    public static <K, D> D getData(RemoteData<K, D> remoteData) {
        return remoteData != null ? remoteData.getData() : null;
    }

    @Override
    public String toString() {
        String toString = key == null ? "" : String.valueOf(key);
        if (ObjectUtil.isNotEmpty(this.data) && this.data instanceof String) {
            toString = String.valueOf(data);
        }
        return toString;
    }
}
