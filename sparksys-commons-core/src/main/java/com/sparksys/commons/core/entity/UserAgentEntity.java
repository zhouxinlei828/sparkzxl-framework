package com.sparksys.commons.core.entity;

import lombok.Data;

@Data
public class UserAgentEntity {

    private String ua;

    /**
     * 请求ip
     */
    private String requestIp;

    /**
     * 登录地点
     */
    private String location;

    /**
     * 浏览器名称
     */
    private String browser;

    /**
     * 浏览器版本
     */
    private String browserVersion;

    /**
     * 操作系统
     */
    private String operatingSystem;

}
