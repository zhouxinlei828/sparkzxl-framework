package com.github.sparkzxl.gateway.plugin.rule;

import com.github.sparkzxl.core.jackson.JsonUtil;

/**
 * description: the RuleHandle interface
 *
 * @author zhouxinlei
 * @date 2022-01-10 13:44:48
 */
public interface RuleHandle {

    /**
     * Format this object to json string.
     *
     * @return json string.
     */
    default String toJson() {
        return JsonUtil.toJson(this);
    }
}