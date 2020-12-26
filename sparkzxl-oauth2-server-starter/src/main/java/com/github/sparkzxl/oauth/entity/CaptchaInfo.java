package com.github.sparkzxl.oauth.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * description: 验证码信息
 *
 * @author: zhouxinlei
 * @date: 2020-12-26 22:41:02
 */
@Data
@ApiModel("验证码信息")
public class CaptchaInfo {

    @ApiModelProperty("key")
    private String key;

    @ApiModelProperty("验证码")
    private String data;

}
