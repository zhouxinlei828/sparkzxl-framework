package com.github.sparkzxl.gateway.plugin.common.entity;

import com.github.sparkzxl.gateway.plugin.rule.RuleData;
import lombok.Data;

import java.io.Serializable;

/**
 * description: filter data
 *
 * @author zhouxinlei
 * @date 2022-01-10 09:51:35
 */
@Data
public class FilterData implements Serializable {

    private static final long serialVersionUID = -7820961775260505451L;

    private boolean enabled;

    private String name;

    private String config;

    private RuleData rule;

}