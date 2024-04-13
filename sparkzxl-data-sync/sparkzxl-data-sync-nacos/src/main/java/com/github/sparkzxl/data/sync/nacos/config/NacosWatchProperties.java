package com.github.sparkzxl.data.sync.nacos.config;


import lombok.Getter;
import lombok.Setter;

/**
 * description: nacos watcher Data properties
 *
 * @author zhouxinlei
 * @since 2022-09-06 10:38:18
 */
@Setter
@Getter
public class NacosWatchProperties {

    private String dataId;

    private String group;

}
