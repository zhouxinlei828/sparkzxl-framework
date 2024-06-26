package com.github.sparkzxl.data.sync.admin.listener.websocket;


import cn.hutool.core.util.IdUtil;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.data.sync.admin.listener.DataChangedListener;
import com.github.sparkzxl.data.sync.common.entity.PushData;
import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * description: The type Websocket data changed listener.
 *
 * @author zhouxinlei
 * @since 2022-08-25 10:48:10
 */
public class WebsocketDataChangedListener implements DataChangedListener {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketDataChangedListener.class);

    @Override
    public <T> void onChanged(String configGroup, String eventType, List<T> data) {
        String syncId = IdUtil.fastSimpleUUID();
        logger.info("onDataChanged，syncId:{}，configGroup:{}，eventType:{}，size:{}", syncId, configGroup, eventType, data.size());
        PushData<?> configData = new PushData<>(syncId, configGroup, eventType, data);
        WebsocketCollector.send(JsonUtils.getJson().toJson(configData), DataEventTypeEnum.acquireByName(eventType));
    }
}
