package com.github.sparkzxl.open.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * description: token信息
 *
 * @author: zhouxinlei
 * @date: '2021-03-10 17:18:06'
 */
@ApiModel("token信息")
@Data
public class AccessTokenInfo implements Serializable {

    private static final long serialVersionUID = -7651157571646582864L;

    @ApiModelProperty("令牌信息")
    private String accessToken;

    @ApiModelProperty("token类型")
    private String tokenType;

    @ApiModelProperty("刷新令牌")
    private String refreshToken;

    @ApiModelProperty("有效期")
    private Date expiration;

    @ApiModelProperty("租户")
    private String tenant;


}
