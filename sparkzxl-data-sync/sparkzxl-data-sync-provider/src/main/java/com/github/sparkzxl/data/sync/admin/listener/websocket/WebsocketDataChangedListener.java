package com.github.sparkzxl.data.sync.admin.listener.websocket;


import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.data.sync.admin.listener.DataChangedListener;
import com.github.sparkzxl.data.sync.common.entity.MetaData;
import com.github.sparkzxl.data.sync.common.entity.WebsocketData;
import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;
import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;

import java.util.List;

/**
 * description: The type Websocket data changed listener.
 *
 * @author zhouxinlei
 * @since 2022-08-25 10:48:10
 */
public class WebsocketDataChangedListener implements DataChangedListener {

    @Override
    public void onMetaDataChanged(final List<MetaData> metaDataList, final DataEventTypeEnum eventType) {
        WebsocketData<MetaData> configData =
                new WebsocketData<>(ConfigGroupEnum.META_DATA.name(), eventType.name(), metaDataList);
        WebsocketCollector.send(JSON.toJSONString(configData), eventType);
    }
}
