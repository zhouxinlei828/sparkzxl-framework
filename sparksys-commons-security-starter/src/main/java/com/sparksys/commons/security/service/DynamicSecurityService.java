package com.sparksys.commons.security.service;


import org.springframework.security.access.ConfigAttribute;

import java.util.Map;

/**
 * description：动态权限 服务类
 *
 * @author： zhouxinlei
 * @date： 2020-06-24 11:32:55
 */
public interface DynamicSecurityService {

    /**
     * 加载资源ANT通配符和资源对应MAP
     *
     * @return Map<String, ConfigAttribute>
     */
    Map<String, ConfigAttribute> loadDataSource();

}
