package com.github.sparkzxl.data.sync.admin.listener.zookeeper;

import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.data.sync.admin.config.zookeeper.ZkWatchProperties;
import com.github.sparkzxl.data.sync.admin.listener.AbstractDataChangedInit;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;

/**
 * description: The type Zookeeper data changed init.
 *
 * @author zhouxinlei
 * @since 2022-09-08 08:53:11
 */
public class ZookeeperDataChangedInit extends AbstractDataChangedInit {

    private final CuratorFramework curatorFramework;

    private final List<ZkWatchProperties> watchConfigs;

    public ZookeeperDataChangedInit(CuratorFramework curatorFramework,
            List<ZkWatchProperties> watchConfigs) {
        this.curatorFramework = curatorFramework;
        this.watchConfigs = watchConfigs;
    }

    @Override
    protected boolean notExist() {
        boolean exists = false;
        try {
            for (ZkWatchProperties watchConfig : watchConfigs) {
                exists = null != curatorFramework.checkExists().forPath(watchConfig.getPath());
            }
        } catch (Exception e) {
            throw new BizException(e.getMessage());
        }
        return exists;
    }
}
