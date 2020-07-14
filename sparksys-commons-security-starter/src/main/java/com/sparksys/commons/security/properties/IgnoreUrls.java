package com.sparksys.commons.security.properties;

import lombok.Data;

import java.util.List;

/**
 * description: 用于配置不需要保护的资源路径
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:38:12
 */
@Data
public class IgnoreUrls {

    private List<String> urls;

}
