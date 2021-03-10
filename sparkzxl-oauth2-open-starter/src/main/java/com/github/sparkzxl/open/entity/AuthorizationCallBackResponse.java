package com.github.sparkzxl.open.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * description:
 *
 * @author: zhouxinlei
 * @date: 2020-08-04 15:26:10
 */
@Data
@ApiModel(value = "授权码返回参数")
public class AuthorizationCallBackResponse {

    @ApiModelProperty(value = "前端地址")
    private String frontUrl;

    @ApiModelProperty(value = "登录态")
    private String loginState;

}
