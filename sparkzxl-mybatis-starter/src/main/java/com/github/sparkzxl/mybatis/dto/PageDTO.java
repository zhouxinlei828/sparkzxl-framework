package com.github.sparkzxl.mybatis.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * description: 分页入参
 *
 * @author zhouxinlei
 */
@Data
public class PageDTO {

    @ApiModelProperty(value = "当前页", example = "1")
    @Min(1)
    private Integer pageNum;

    @ApiModelProperty(value = "分页大小", example = "10")
    @Max(value = 2000)
    private Integer pageSize;

}
