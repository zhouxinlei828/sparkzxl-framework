package com.github.sparkzxl.log.properties;

import lombok.Data;

/**
 * description: kafka配置
 *
 * @author zhouxinlei
 */
@Data
public class KafkaProperties {

    /**
     * kafka是否开启
     */
    private boolean enable;

    /**
     * kafka地址
     */
    private String servers;

    /**
     * kafka topic
     */
    private String topic;


}
