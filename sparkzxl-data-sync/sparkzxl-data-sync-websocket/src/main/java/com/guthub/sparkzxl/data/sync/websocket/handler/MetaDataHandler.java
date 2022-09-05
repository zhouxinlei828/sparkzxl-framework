package com.guthub.sparkzxl.data.sync.websocket.handler;


import com.alibaba.fastjson.JSONArray;
import com.github.sparkzxl.data.sync.api.DataSubscriber;
import com.github.sparkzxl.data.sync.api.MetaDataSubscriber;
import com.github.sparkzxl.data.sync.common.entity.MetaData;
import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;

import java.util.List;

/**
 * description: The type MetaData handler.
 *
 * @author zhouxinlei
 * @since 2022-08-25 11:29:42
 */
public class MetaDataHandler extends AbstractDataHandler<MetaData> {

    public MetaDataHandler(final List<MetaDataSubscriber> subscribers) {
        super(subscribers);
    }

    @Override
    public List<MetaData> convert(final String json) {
        return JSONArray.parseArray(json, MetaData.class);
    }

    @Override
    protected void doRefresh(final List<MetaData> dataList) {
        subscribers.forEach(DataSubscriber::refresh);
        dataList.forEach(metaData -> subscribers.forEach(metaDataSubscriber -> metaDataSubscriber.onSubscribe(metaData)));
    }

    @Override
    protected void doUpdate(final List<MetaData> dataList) {
        dataList.forEach(metaData -> subscribers.forEach(metaDataSubscriber -> metaDataSubscriber.onSubscribe(metaData)));
    }

    @Override
    protected void doDelete(final List<MetaData> dataList) {
        dataList.forEach(metaData -> subscribers.forEach(metaDataSubscriber -> metaDataSubscriber.unSubscribe(metaData)));
    }

    @Override
    public String configGroup() {
        return ConfigGroupEnum.META_DATA.name();
    }
}
