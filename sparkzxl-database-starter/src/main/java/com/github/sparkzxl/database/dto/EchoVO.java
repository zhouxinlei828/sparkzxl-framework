package com.github.sparkzxl.database.dto;

import java.util.Map;

/**
 * description: 注入VO 父类
 *
 * @author zhouxinlei
 * @date 2021-06-09 10:24:25
 */
public interface EchoVO {

    /**
     * 回显值 集合
     *
     * @return 回显值 集合
     */
    Map<String, Object> getEchoMap();
}
