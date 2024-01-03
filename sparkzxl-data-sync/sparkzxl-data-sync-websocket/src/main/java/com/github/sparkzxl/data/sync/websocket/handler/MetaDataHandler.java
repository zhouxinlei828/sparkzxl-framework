package com.github.sparkzxl.data.sync.websocket.handler;

import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.data.sync.api.DataSubscriber;
import com.github.sparkzxl.data.sync.api.MetaDataSubscriber;
import com.github.sparkzxl.data.sync.common.entity.MetaData;
import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;

import java.util.List;

/**
 * description: The type MetaData handler.
 *
 * @author zhouxinlei
 * @since 2023-12-27 17:39:04
 */
public class MetaDataHandler extends AbstractDataHandler<MetaData> {

    public MetaDataHandler(List<MetaDataSubscriber> dataSubscribers) {
        super(dataSubscribers);
    }

    @Override
    protected List<MetaData> convert(String json) {
        return JsonUtils.getJson().toJavaList(json, MetaData.class);
    }

    @Override
    protected void doRefresh(String syncId, List<MetaData> dataList) {
        subscribers.forEach(DataSubscriber::refresh);
        dataList.forEach(data -> subscribers.forEach(dataSubscriber -> dataSubscriber.onSubscribe(syncId, data)));
    }

    @Override
    protected void doUpdate(String syncId, List<MetaData> dataList) {
        dataList.forEach(data -> subscribers.forEach(dataSubscriber -> dataSubscriber.onSubscribe(syncId, data)));
    }

    @Override
    protected void doDelete(String syncId, List<MetaData> dataList) {
        dataList.forEach(data -> subscribers.forEach(dataSubscriber -> dataSubscriber.unSubscribe(syncId, data)));
    }

    @Override
    public String configGroup() {
        return ConfigGroupEnum.META_DATA.getCode();
    }
}
