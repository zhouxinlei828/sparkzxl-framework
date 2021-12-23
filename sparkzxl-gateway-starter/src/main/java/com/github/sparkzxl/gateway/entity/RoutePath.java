package com.github.sparkzxl.gateway.entity;

import lombok.Data;

/**
 * description: 路由路径实体类
 *
 * @author zhouxinlei
 * @date 2021-12-23 10:09
 */
@Data
public class RoutePath {

    /**
     * 路由id
     */
    private String routeId;

    /**
     * 请求路径
     */
    private String url;

    /**
     * 相对路径
     */
    private String path;


}
