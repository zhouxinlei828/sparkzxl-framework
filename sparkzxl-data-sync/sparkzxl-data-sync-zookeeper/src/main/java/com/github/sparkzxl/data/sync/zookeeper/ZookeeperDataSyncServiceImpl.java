package com.github.sparkzxl.data.sync.zookeeper;

import cn.hutool.core.text.StrPool;
import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import com.github.sparkzxl.data.sync.api.DataSubscriber;
import com.github.sparkzxl.data.sync.api.DataSyncService;
import com.github.sparkzxl.data.sync.zookeeper.config.ZkWatchProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description: this cache data with zookeeper.
 *
 * @author zhouxinlei
 * @since 2022-09-08 11:05:36
 */
public class ZookeeperDataSyncServiceImpl implements DataSyncService {

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperDataSyncServiceImpl.class);

    private final CuratorFramework curatorFramework;
    private final Map<String, List<DataSubscriber>> dataSubscriberMap = Maps.newConcurrentMap();
    private final Map<String, CuratorCache> curatorCacheMap = Maps.newConcurrentMap();
    private final Map<String, TreeCacheListener> treeCacheListenerConcurrentMap = Maps.newConcurrentMap();
    private List<ZkWatchProperties> watchConfigs;

    public ZookeeperDataSyncServiceImpl(CuratorFramework curatorFramework,
                                        List<DataSubscriber> dataSubscribers,
                                        List<ZkWatchProperties> watchConfigs) {
        this.curatorFramework = curatorFramework;
        dataSubscriberMap.putAll(dataSubscribers.stream().collect(Collectors.groupingBy(DataSubscriber::group)));
        this.watchConfigs = watchConfigs;
        start();
    }

    /**
     * Start.
     */
    public void start() {
        for (ZkWatchProperties watchConfig : watchConfigs) {
            addCache(formatPath(watchConfig.getPath()), (TreeCacheListener) null);
        }
    }

    public CuratorCache addCache(final String path, final TreeCacheListener... listeners) {
        CuratorCache curatorCache = CuratorCache.build(curatorFramework, path);
        curatorCacheMap.put(path, curatorCache);
        for (TreeCacheListener listener : listeners) {
            CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder().forTreeCache(curatorFramework, listener).build();
            curatorCache.listenable().addListener(curatorCacheListener);
        }
        try {
            curatorCache.start();
        } catch (Exception e) {
            logger.error("Failed to start curator cache", e);
            throw new RuntimeException("failed to add curator cache.", e);
        }
        return curatorCache;
    }

    @Override
    public void close() throws Exception {

    }

    public String formatPath(String path) {
        String result = path;
        if (!path.startsWith(StrPool.SLASH)) {
            result = StrPool.SLASH.concat(path);
        }
        return result;
    }
}
