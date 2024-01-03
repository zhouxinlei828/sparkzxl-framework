package com.github.sparkzxl.data.sync.websocket.handler;

import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.data.sync.api.BusinessDataSubscriber;
import com.github.sparkzxl.data.sync.api.DataSubscriber;
import com.github.sparkzxl.data.sync.common.entity.BusinessData;
import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;

import java.util.List;

/**
 * description: The type business data handler.
 *
 * @author zhouxinlei
 * @since 2023-12-27 17:39:04
 */
public class BusinessDataHandler extends AbstractDataHandler<BusinessData> {

    public BusinessDataHandler(List<BusinessDataSubscriber> dataSubscribers) {
        super(dataSubscribers);
    }

    @Override
    protected List<BusinessData> convert(String json) {
        return JsonUtils.getJson().toJavaList(json, BusinessData.class);
    }

    @Override
    protected void doRefresh(String syncId, List<BusinessData> dataList) {
        subscribers.forEach(DataSubscriber::refresh);
        dataList.forEach(data -> subscribers.forEach(dataSubscriber -> dataSubscriber.onSubscribe(syncId, data)));
    }

    @Override
    protected void doUpdate(String syncId, List<BusinessData> dataList) {
        dataList.forEach(data -> subscribers.forEach(dataSubscriber -> dataSubscriber.onSubscribe(syncId, data)));
    }

    @Override
    protected void doDelete(String syncId, List<BusinessData> dataList) {
        dataList.forEach(data -> subscribers.forEach(dataSubscriber -> dataSubscriber.unSubscribe(syncId, data)));
    }

    @Override
    public String configGroup() {
        return ConfigGroupEnum.BUSINESS_DATA.getCode();
    }
}
