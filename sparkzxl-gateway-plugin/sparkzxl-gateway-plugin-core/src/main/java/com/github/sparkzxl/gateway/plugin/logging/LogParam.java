package com.github.sparkzxl.gateway.plugin.logging;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * description: 日志参数实体类
 *
 * @author zhouxinlei
 */
@Data
@Accessors(chain = true)
public class LogParam implements Serializable {

    private static final long serialVersionUID = -3240153643271750465L;

    private String tenantId;

    private String username;

    private String trace;

    private String ip;

    private String httpMethod;

    private Integer httpStatus;

    private String path;

    private String host;

    private String routeId;

    private LocalDateTime reqTime;

    private String headers;

    private String reqBody;

    private String queryParams;

    private String reqFormData;

    private String respBody;

    private String timeCost;

}
