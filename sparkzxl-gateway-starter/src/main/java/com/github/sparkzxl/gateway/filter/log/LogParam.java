package com.github.sparkzxl.gateway.filter.log;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * description: 日志参数实体类
 *
 * @author zhouxinlei
 * @date 2021-11-26 13:53
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "LogParam", description = "日志参数实体类")
public class LogParam implements Serializable {

    private static final long serialVersionUID = -1015789970080558328L;

    private String username;

    private String trace;

    private String ip;

    private HttpMethod httpMethod;

    private Integer httpStatus;

    private String path;

    private String host;

    private String routeId;

    private String routerToUri;

    private LocalDateTime reqTime;

    private String reqBody;

    private String queryParams;

    private String reqFormData;

    private String respBody;

    private String timeCost;

}
