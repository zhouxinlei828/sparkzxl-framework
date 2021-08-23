package com.github.sparkzxl.service.wechat;

import lombok.Data;

import java.util.Map;

/**
 * @author weilai
 */
@Data
public class WorkWeXinSendRequest {

    private String touser;

    private Integer agentid;

    private String msgtype;

    private Map<String, Object> text;
}
