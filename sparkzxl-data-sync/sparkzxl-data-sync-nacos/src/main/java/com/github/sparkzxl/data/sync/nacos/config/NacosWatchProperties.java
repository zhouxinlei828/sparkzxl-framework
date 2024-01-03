package com.github.sparkzxl.data.sync.nacos.config;


/**
 * description: nacos watcher Data properties
 *
 * @author zhouxinlei
 * @since 2022-09-06 10:38:18
 */
public class NacosWatchProperties {

    private String dataId;

    private String group;

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
