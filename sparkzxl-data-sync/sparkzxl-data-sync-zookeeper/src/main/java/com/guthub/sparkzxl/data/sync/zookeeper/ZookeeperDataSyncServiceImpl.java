package com.guthub.sparkzxl.data.sync.zookeeper;

import com.github.sparkzxl.data.sync.api.DataSubscriber;
import com.github.sparkzxl.data.sync.api.DataSyncService;
import com.google.common.collect.Maps;
import org.apache.curator.framework.CuratorFramework;
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

    public ZookeeperDataSyncServiceImpl(CuratorFramework curatorFramework,
                                        List<DataSubscriber> dataSubscribers) {
        this.curatorFramework = curatorFramework;
        dataSubscriberMap.putAll(dataSubscribers.stream().collect(Collectors.groupingBy(DataSubscriber::group)));
        start();
    }

    /**
     *
     * Start.
     */
    public void start() {

    }


    @Override
    public void close() throws Exception {

    }
}
