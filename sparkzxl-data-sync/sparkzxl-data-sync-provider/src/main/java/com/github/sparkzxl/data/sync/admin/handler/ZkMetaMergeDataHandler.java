package com.github.sparkzxl.data.sync.admin.handler;

import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.data.sync.admin.DataSyncPushType;
import com.github.sparkzxl.data.sync.common.entity.MetaData;
import com.github.sparkzxl.data.sync.common.entity.PushData;
import com.github.sparkzxl.data.sync.common.enums.ConfigGroupEnum;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * description: ZK元数据合并处理
 *
 * @author zhouxinlei
 * @since 2022-09-05 14:50:02
 */
public class ZkMetaMergeDataHandler implements MergeDataHandler<MetaData> {

    @Override
    public Map<String, MetaData> handle(PushData<MetaData> pushData) {
        Map<String, MetaData> metaDataMap = Maps.newConcurrentMap();
        List<MetaData> dataList = pushData.getData();
        dataList.forEach(meta -> metaDataMap.put(getKey(meta), meta));
        return metaDataMap;
    }

    @Override
    public String configGroup() {
        return ConfigGroupEnum.META_DATA.getCode().concat(StrPool.COLON).concat(DataSyncPushType.ZOOKEEPER.name().toLowerCase(Locale.ROOT));
    }

    private String getKey(MetaData metaData) {
        return MessageFormat.format("{0}-{1}-{2}-{3}",
                StringUtils.isEmpty(metaData.getTenantId()) ? "default" : metaData.getTenantId(),
                StringUtils.isEmpty(metaData.getAreaCode()) ? "default" : metaData.getAreaCode(),
                metaData.getDataType(),
                metaData.getDataValue());
    }

}
