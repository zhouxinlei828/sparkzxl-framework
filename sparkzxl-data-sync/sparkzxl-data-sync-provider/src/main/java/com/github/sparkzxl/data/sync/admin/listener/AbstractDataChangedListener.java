package com.github.sparkzxl.data.sync.admin.listener;

import com.github.sparkzxl.data.sync.common.entity.PushData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * description: AbstractDataChangedListener
 *
 * @author zhouxinlei
 * @since 2022-09-05 10:06:15
 */
public abstract class AbstractDataChangedListener implements DataChangedListener {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataChangedListener.class);

    @Override
    public <T> void onChanged(String configGroup, String eventType, List<T> data) {
        logger.info("onDataChanged，configGroup:{}，eventType:{}，size:{}", configGroup, eventType, data.size());
        PushData<?> configData = new PushData<>(configGroup, eventType, data);
        publishConfig(configData);
    }

    /**
     * 推送配置信息
     *
     * @param pushData pushData
     */
    public abstract void publishConfig(PushData<?> pushData);

}
