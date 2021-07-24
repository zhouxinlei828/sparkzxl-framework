package com.github.sparkzxl.entity.data;

import cn.hutool.core.util.ObjectUtil;
import lombok.*;

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
@EqualsAndHashCode
public class RemoteData<K, D> implements Serializable {

    private K key;
    private D data;


    public RemoteData(K key) {
        this.key = key;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public D getData() {
        return data;
    }

    public void setData(D data) {
        this.data = data;
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
