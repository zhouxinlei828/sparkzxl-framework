package com.github.sparkzxl.dto;

import com.github.sparkzxl.constant.EntityConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

/**
 * description: 分页参数
 *
 * @author zhouxinlei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "PageParams", description = "分页参数")
public class PageParams<T> extends PageDTO {

    @ApiModelProperty(value = "查询参数", required = true)
    private T model;

    @ApiModelProperty(value = "排序,默认createTime", allowableValues = "id,createTime,updateTime", example = "id")
    private String sort = EntityConstant.ID;

    @ApiModelProperty("扩展参数")
    private Map<String, String> extra = new HashMap<>(16);

}
