package com.guthub.sparkzxl.data.sync.nacos.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * description: nacos watcher Data properties
 *
 * @author zhouxinlei
 * @since 2022-09-06 10:38:18
 */
@NoArgsConstructor
@Data
public class NacosWatchProperties {

    @JsonProperty("dataId")
    private String dataId;

    @JsonProperty("group")
    private String group;
}
