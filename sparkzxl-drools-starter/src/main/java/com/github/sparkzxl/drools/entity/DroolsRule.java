package com.github.sparkzxl.drools.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * description: drools 规则父类
 *
 * @author zhouxinlei
 * @date 2021-08-05 15:51:27
 */
@ApiModel(value = "DroolsRule", description = "drools规则父类")
@Data
public class DroolsRule {

    /**
     * 分组
     */
    @ApiModelProperty(value = "分组")
    private String agendaGroup;
    /**
     * 结果查询
     */
    @ApiModelProperty(value = "结果查询")
    private String resultQuery;
    /**
     * 结果属性对象
     */
    @ApiModelProperty(value = "结果属性对象")
    private String resultVariable;

    /**
     * 类名
     */
    @ApiModelProperty(value = "结果类名")
    private String className;

}

