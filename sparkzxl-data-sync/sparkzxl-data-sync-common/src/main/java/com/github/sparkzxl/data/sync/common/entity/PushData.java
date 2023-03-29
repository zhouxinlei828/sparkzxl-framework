package com.github.sparkzxl.data.sync.common.entity;

import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;
import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;
import java.util.List;
import java.util.Objects;

/**
 * description:Data set, including {@link MetaData}.
 *
 * @author zhouxinlei
 * @since 2022-08-25 10:54:00
 */
public class PushData<T> {

    /**
     * config group. {@linkplain ConfigGroupEnum}
     */
    private String configGroup;

    /**
     * event type. {@linkplain DataEventTypeEnum}
     */
    private String eventType;

    /**
     * data list. {@link MetaData}.
     */
    private List<T> data;

    /**
     * no args constructor.
     */
    public PushData() {
    }

    /**
     * all args constructor.
     *
     * @param configGroup config group
     * @param eventType   eventType
     * @param data        data
     */
    public PushData(final String configGroup, final String eventType, final List<T> data) {
        this.configGroup = configGroup;
        this.eventType = eventType;
        this.data = data;
    }

    public String getConfigGroup() {
        return configGroup;
    }

    public PushData<T> setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
        return this;
    }

    /**
     * get eventType.
     *
     * @return eventType
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * set eventType.
     *
     * @param eventType eventType
     * @return this
     */
    public PushData<T> setEventType(final String eventType) {
        this.eventType = eventType;
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
    public PushData<T> setData(final List<T> data) {
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
        PushData<?> that = (PushData<?>) o;
        return Objects.equals(configGroup, that.configGroup) && Objects.equals(eventType, that.eventType) && Objects.equals(data,
                that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(configGroup, eventType, data);
    }

    @Override
    public String toString() {
        return "PushData{"
                + "configGroup='"
                + configGroup
                + '\''
                + ", eventType='"
                + eventType
                + '\''
                + ", data="
                + data
                + '}';
    }
}
