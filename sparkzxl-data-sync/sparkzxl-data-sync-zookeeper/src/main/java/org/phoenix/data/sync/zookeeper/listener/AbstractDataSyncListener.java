package org.phoenix.data.sync.zookeeper.listener;

import cn.hutool.core.text.StrPool;
import com.google.common.base.Strings;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

/**
 * description: Zookeeper Abstract Data Sync Listener.
 *
 * @author zhouxinlei
 * @since 2022-09-09 10:12:47
 */
public abstract class AbstractDataSyncListener implements TreeCacheListener {

    @Override
    public final void childEvent(final CuratorFramework client, final TreeCacheEvent event) {
        ChildData childData = event.getData();
        if (null == childData) {
            return;
        }
        String path = childData.getPath();
        if (Strings.isNullOrEmpty(path)) {
            return;
        }
        event(event.getType(), path, childData);
    }

    /**
     * data sync event.
     *
     * @param type tree cache event type.
     * @param path tree cache event path.
     * @param data tree cache event data.
     */
    protected abstract void event(TreeCacheEvent.Type type, String path, ChildData data);

    public String configGroup() {
        return StrPool.SLASH.concat(getType());
    }

    /**
     * 数据类型
     *
     * @return String
     */
    protected abstract String getType();
}
