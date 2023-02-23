package com.github.sparkzxl.gateway.plugin.dubbo.entity;


import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * description: dubbo request entity
 *
 * @author zhouxinlei
 * @since 2023-01-11 16:44:41
 */
@Data
public class DubbboRequest implements Serializable {

    private String service;

    private String method;

    private Map<String, Object> bodyParameters;

    private String version;
}
