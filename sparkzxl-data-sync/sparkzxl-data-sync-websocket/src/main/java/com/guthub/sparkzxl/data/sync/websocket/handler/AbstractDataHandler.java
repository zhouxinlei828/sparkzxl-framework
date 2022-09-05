package com.guthub.sparkzxl.data.sync.websocket.handler;

import com.github.sparkzxl.data.sync.api.DataSubscriber;
import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;
import org.apache.commons.collections4.CollectionUtils;

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
     * @param dataList the data list
     */
    protected abstract void doRefresh(List<T> dataList);

    /**
     * Do update.
     *
     * @param dataList the data list
     */
    protected abstract void doUpdate(List<T> dataList);

    /**
     * Do delete.
     *
     * @param dataList the data list
     */
    protected abstract void doDelete(List<T> dataList);

    @Override
    public void handle(final String json, final String eventType) {
        List<T> dataList = convert(json);
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        DataEventTypeEnum eventTypeEnum = DataEventTypeEnum.acquireByName(eventType);
        switch (eventTypeEnum) {
            case REFRESH:
            case MYSELF:
                doRefresh(dataList);
                break;
            case UPDATE:
            case CREATE:
                doUpdate(dataList);
                break;
            case DELETE:
                doDelete(dataList);
                break;
            default:
                break;
        }
    }
}
