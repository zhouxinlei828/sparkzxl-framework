package com.github.sparkzxl.open.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Data
@ApiModel(value = "授权请求参数")
public class AuthorizationRequest {

    @ApiModelProperty(value = "授权类型：password，authorization_code，refresh_token", required = true, example = "password")
    private String grantType;

    @ApiModelProperty(value = "授权类型为authorization_code,参数必传", allowEmptyValue = true)
    private String code;

    @ApiModelProperty(value = "授权类型为authorization_code,参数必传", allowEmptyValue = true)
    private String clientId;

    @ApiModelProperty(value = "授权类型为authorization_code,参数必传", allowEmptyValue = true)
    private String clientSecret;

    @ApiModelProperty(value = "授权成功跳转地址,授权类型为authorization_code,参数必传", allowEmptyValue = true)
    private String redirectUri;

    @ApiModelProperty(value = "刷新token，授权类型为refresh_token,参数必传", allowEmptyValue = true)
    private String refreshToken;

    @ApiModelProperty(value = "用户名，授权类型为password,参数必传", allowEmptyValue = true)
    private String username;

    @ApiModelProperty(value = "密码，授权类型为password,参数必传", allowEmptyValue = true)
    private String password;

    @ApiModelProperty(value = "授权范围", required = true, example = "all")
    private String scope;

    @ApiModelProperty(value = "验证码key")
    private String captchaKey;

    @ApiModelProperty(value = "验证码")
    private String captchaCode;

}
