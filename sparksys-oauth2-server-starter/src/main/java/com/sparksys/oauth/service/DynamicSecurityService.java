package com.sparksys.oauth.service;

import org.springframework.security.access.ConfigAttribute;

import java.util.Map;

/**
 * description: 权限资源服务类
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 19:22:40
 */
public interface DynamicSecurityService {

    /**
     * 加载权限资源
     *
     * @return Map<String, ConfigAttribute>
     */
    Map<String, ConfigAttribute> loadDataSource();
}
