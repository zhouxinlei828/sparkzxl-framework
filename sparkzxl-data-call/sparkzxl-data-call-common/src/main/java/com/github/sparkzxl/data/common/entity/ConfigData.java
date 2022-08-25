package com.github.sparkzxl.data.common.entity;

import com.alibaba.fastjson.JSON;

import java.util.List;
import java.util.Objects;

/**
 * description: Data set, including {{@link MetaData}.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:12:05
 */
public class ConfigData<T> {

    private String md5;

    private long lastModifyTime;

    private List<T> data;

    /**
     * no args constructor.
     */
    public ConfigData() {
    }

    /**
     * all args constructor.
     *
     * @param md5            md5
     * @param lastModifyTime lastModifyTime
     * @param data           data
     */
    public ConfigData(final String md5, final long lastModifyTime, final List<T> data) {
        this.md5 = md5;
        this.lastModifyTime = lastModifyTime;
        this.data = data;
    }

    /**
     * get md5.
     *
     * @return md5
     */
    public String getMd5() {
        return md5;
    }

    /**
     * set md5.
     *
     * @param md5 md5
     * @return this
     */
    public ConfigData<T> setMd5(final String md5) {
        this.md5 = md5;
        return this;
    }

    /**
     * get lastModifyTime.
     *
     * @return lastModifyTime
     */
    public long getLastModifyTime() {
        return lastModifyTime;
    }

    /**
     * set lastModifyTime.
     *
     * @param lastModifyTime lastModifyTime
     * @return this
     */
    public ConfigData<T> setLastModifyTime(final long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
        return this;
    }

    /**
     * get data.
     *
     * @return data
     */
    public List<T> getData() {
        return data;
    }

    /**
     * set data.
     *
     * @param data data
     * @return this
     */
    public ConfigData<T> setData(final List<T> data) {
        this.data = data;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConfigData<?> that = (ConfigData<?>) o;
        return lastModifyTime == that.lastModifyTime && Objects.equals(md5, that.md5) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(md5, lastModifyTime, data);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
