package com.github.sparkzxl.gateway.rule;

import com.github.sparkzxl.core.json.JsonUtils;

/**
 * description: the RuleHandle interface
 *
 * @author zhouxinlei
 * @since 2022-01-10 13:44:48
 */
public interface RuleHandle {

    /**
     * Format this object to json string.
     *
     * @return json string.
     */
    default String toJson() {
        return JsonUtils.getJson().toJson(this);
    }
}