package com.github.sparkzxl.data.sync.admin.listener.zookeeper;

import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.data.sync.admin.DataSyncPushType;
import com.github.sparkzxl.data.sync.common.constant.ZookeeperPathConstants;
import com.github.sparkzxl.data.sync.common.entity.PushData;
import com.github.sparkzxl.data.sync.common.enums.DataEventTypeEnum;
import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import com.github.sparkzxl.data.sync.admin.handler.MergeDataHandler;
import com.github.sparkzxl.data.sync.admin.listener.AbstractDataChangedListener;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * description: Use Zookeeper to synchronized data changes.
 *
 * @author zhouxinlei
 * @since 2022-09-08 10:05:42
 */
public class ZookeeperDataChangedListener extends AbstractDataChangedListener {

    private final CuratorFramework curatorFramework;
    private final Map<String, MergeDataHandler> mergeDataHandlerMap = Maps.newConcurrentMap();

    public ZookeeperDataChangedListener(CuratorFramework curatorFramework,
                                        List<MergeDataHandler> mergeDataHandlerList) {
        this.curatorFramework = curatorFramework;
        mergeDataHandlerList.forEach(mergeDataHandler -> mergeDataHandlerMap.put(mergeDataHandler.configGroup(), mergeDataHandler));
    }

    @Override
    public void publishConfig(PushData<?> pushData) {
        try {
            DataEventTypeEnum eventType = DataEventTypeEnum.acquireByName(pushData.getEventType());
            MergeDataHandler mergeDataHandler = mergeDataHandlerMap.get(
                    pushData.getConfigGroup().concat(StrPool.COLON).concat(DataSyncPushType.ZOOKEEPER.name().toLowerCase(Locale.ROOT)));
            Map<String, ?> map = mergeDataHandler.handle(pushData);
            for (Map.Entry<String, ?> entry : map.entrySet()) {
                String path = ZookeeperPathConstants.buildPath(URLEncoder.encode(pushData.getConfigGroup(), "UTF-8"),
                        URLEncoder.encode(entry.getKey(), "UTF-8"));
                // delete
                if (eventType == DataEventTypeEnum.DELETE) {
                    deleteZkPath(path);
                    continue;
                }
                // create or update
                insertZkNode(path, entry.getValue());
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertZkNode(final String path, final Object data) {
        String val = "";
        if (data != null) {
            val = JsonUtils.getJson().toJson(data);
        }
        try {
            curatorFramework.create().orSetData().creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(path, val.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteZkPath(final String path) {
        try {
            boolean isExist = null != curatorFramework.checkExists().forPath(path);
            if (isExist) {
                curatorFramework.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
