package com.github.sparkzxl.log.properties;

import lombok.Data;

/**
 * description: 日志文件配置类
 *
 * @author zhouxinlei
 */
@Data
public class FileProperties {

    /**
     * 是否开启日志持久化
     */
    private boolean enable;

    /**
     * 是否开启日志json化存储
     */
    private boolean enableJson;

    private String name;

    private boolean cleanHistoryOnStart;

    private String totalSizeCap = "0B";

    private String path = "logs";

    private int maxHistory = 7;

    private String maxFileSize = "10MB";

}
