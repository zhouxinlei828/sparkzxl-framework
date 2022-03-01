package com.github.sparkzxl.gateway.plugin.rule;

import lombok.Data;

import java.io.Serializable;

/**
 * description: 规则数据
 *
 * @author zhouxinlei
 * @date 2022-01-08 17:16:06
 */
@Data
public class RuleData implements Serializable {

    private static final long serialVersionUID = -5457801649731972976L;

    private boolean enabled;

    private String name;

    private String handle;

}