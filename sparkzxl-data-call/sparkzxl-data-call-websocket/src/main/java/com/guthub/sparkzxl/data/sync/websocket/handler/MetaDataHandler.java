package com.guthub.sparkzxl.data.sync.websocket.handler;


import com.alibaba.fastjson.JSONArray;
import com.github.sparkzxl.data.common.entity.MetaData;
import com.github.sparkzxl.sync.data.api.MetaDataSubscriber;

import java.util.List;

/**
 * description: The type MetaData handler.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:29:42
 */
public class MetaDataHandler extends AbstractDataHandler<MetaData> {

    private final List<MetaDataSubscriber> metaDataSubscribers;

    public MetaDataHandler(final List<MetaDataSubscriber> metaDataSubscribers) {
        this.metaDataSubscribers = metaDataSubscribers;
    }

    @Override
    public List<MetaData> convert(final String json) {
        return JSONArray.parseArray(json,MetaData.class);
    }

    @Override
    protected void doRefresh(final List<MetaData> dataList) {
        metaDataSubscribers.forEach(MetaDataSubscriber::refresh);
        dataList.forEach(metaData -> metaDataSubscribers.forEach(metaDataSubscriber -> metaDataSubscriber.onSubscribe(metaData)));
    }

    @Override
    protected void doUpdate(final List<MetaData> dataList) {
        dataList.forEach(metaData -> metaDataSubscribers.forEach(metaDataSubscriber -> metaDataSubscriber.onSubscribe(metaData)));
    }

    @Override
    protected void doDelete(final List<MetaData> dataList) {
        dataList.forEach(metaData -> metaDataSubscribers.forEach(metaDataSubscriber -> metaDataSubscriber.unSubscribe(metaData)));
    }
}
