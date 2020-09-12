package com.github.sparkzxl.core.entity;

import lombok.Data;

/**
 * description: UserAgent信息
 *
 * @author: zhouxinlei
 * @date: 2020-07-28 17:47:18
 */
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
