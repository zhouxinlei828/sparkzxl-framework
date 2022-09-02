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
public class WebsocketData<T> {

    /**
     * group type.
     * {@linkplain ConfigGroupEnum}
     */
    private String groupType;

    /**
     * event type.
     * {@linkplain DataEventTypeEnum}
     */
    private String eventType;

    /**
     * data list.
     * {@link MetaData}.
     */
    private List<T> data;

    /**
     * no args constructor.
     */
    public WebsocketData() {
    }

    /**
     * all args constructor.
     *
     * @param groupType groupType
     * @param eventType eventType
     * @param data      data
     */
    public WebsocketData(final String groupType, final String eventType, final List<T> data) {
        this.groupType = groupType;
        this.eventType = eventType;
        this.data = data;
    }

    /**
     * get groupType.
     *
     * @return groupType
     */
    public String getGroupType() {
        return groupType;
    }

    /**
     * set groupType.
     *
     * @param groupType groupType
     * @return this
     */
    public WebsocketData<T> setGroupType(final String groupType) {
        this.groupType = groupType;
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
    public WebsocketData<T> setEventType(final String eventType) {
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
    public WebsocketData<T> setData(final List<T> data) {
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
        WebsocketData<?> that = (WebsocketData<?>) o;
        return Objects.equals(groupType, that.groupType) && Objects.equals(eventType, that.eventType) && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupType, eventType, data);
    }

    @Override
    public String toString() {
        return "WebsocketData{"
                + "groupType='"
                + groupType
                + '\''
                + ", eventType='"
                + eventType
                + '\''
                + ", data="
                + data
                + '}';
    }
}