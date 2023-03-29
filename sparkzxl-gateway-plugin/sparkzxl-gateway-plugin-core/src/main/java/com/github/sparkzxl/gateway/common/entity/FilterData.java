package com.github.sparkzxl.gateway.common.entity;

import com.github.sparkzxl.gateway.rule.RuleData;
import java.io.Serializable;
import lombok.Data;

/**
 * description: filter data
 *
 * @author zhouxinlei
 * @since 2022-01-10 09:51:35
 */
@Data
public class FilterData implements Serializable {

    private static final long serialVersionUID = -7820961775260505451L;

    private boolean enabled;

    private String name;

    private String config;

    private RuleData rule;

}
