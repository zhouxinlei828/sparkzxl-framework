package com.github.sparkzxl.database.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * description: 分页入参
 *
 * @author zhouxinlei
 */
@Data
public class PageDTO {

    @ApiModelProperty(value = "当前页", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "页面大小", example = "10")
    private Integer pageSize = 10;

}
