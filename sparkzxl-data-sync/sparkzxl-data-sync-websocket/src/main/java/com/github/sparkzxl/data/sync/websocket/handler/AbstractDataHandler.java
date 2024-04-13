package com.github.sparkzxl.data.sync.websocket.handler;

import org.apache.commons.collections4.CollectionUtils;
import com.github.sparkzxl.data.sync.api.DataSubscriber;
import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;

import java.util.List;

/**
 * description: The type Abstract data handler.
 *
 * @param <T> the type parameter
 * @author zhouxinlei
 * @since 2022-08-25 13:42:07
 */
public abstract class AbstractDataHandler<T> implements DataHandler {


    protected final List<? extends DataSubscriber<T>> subscribers;

    protected AbstractDataHandler(List<? extends DataSubscriber<T>> subscribers) {
        this.subscribers = subscribers;
    }

    /**
     * Convert list.
     *
     * @param json the json
     * @return the list
     */
    protected abstract List<T> convert(String json);

    /**
     * Do refresh.
     *
     * @param syncId   synchronization id
     * @param dataList the data list
     */
    protected abstract void doRefresh(String syncId, List<T> dataList);

    /**
     * Do update.
     *
     * @param syncId   synchronization id
     * @param dataList the data list
     */
    protected abstract void doUpdate(String syncId, List<T> dataList);

    /**
     * Do delete.
     *
     * @param syncId   synchronization id
     * @param dataList the data list
     */
    protected abstract void doDelete(String syncId, List<T> dataList);

    @Override
    public void handle(final String syncId, final String json, final String eventType) {
        List<T> dataList = convert(json);
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        DataEventTypeEnum eventTypeEnum = DataEventTypeEnum.acquireByName(eventType);
        switch (eventTypeEnum) {
            case REFRESH:
            case MYSELF:
                doRefresh(syncId, dataList);
                break;
            case UPDATE:
            case CREATE:
                doUpdate(syncId, dataList);
                break;
            case DELETE:
                doDelete(syncId, dataList);
                break;
            default:
                break;
        }
    }
}
