package com.github.sparkzxl.database.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * description: 分页入参
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:21:22
 */
@Data
public class PageDTO {

    @ApiModelProperty(value = "当前页", example = "1")
    private int pageNum = 1;

    @ApiModelProperty(value = "页面大小", example = "10")
    private int pageSize = 10;

}
