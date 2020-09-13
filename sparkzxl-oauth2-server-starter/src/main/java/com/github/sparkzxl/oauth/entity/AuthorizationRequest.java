package com.github.sparkzxl.oauth.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
@ApiModel(value = "授权请求参数")
public class AuthorizationRequest {

    @ApiModelProperty(value = "授权类型：password，authorization_code，refresh_token", required = true, example = "password")
    @JsonProperty(value = "grant_type")
    private String grantType;

    @ApiModelProperty(value = "授权类型为authorization_code,参数必传", allowEmptyValue = true)
    private String code;

    @ApiModelProperty(value = "授权类型为authorization_code,参数必传", allowEmptyValue = true)
    @JsonProperty("client_id")
    private String clientId;

    @ApiModelProperty(value = "授权成功跳转地址,授权类型为authorization_code,参数必传", allowEmptyValue = true)
    @JsonProperty("redirect_uri")
    private String redirectUri;

    @ApiModelProperty(value = "刷新token，授权类型为refresh_token,参数必传", allowEmptyValue = true)
    @JsonProperty("refresh_token")
    private String refreshToken;

    @ApiModelProperty(value = "用户名，授权类型为password,参数必传", allowEmptyValue = true)
    private String username;

    @ApiModelProperty(value = "密码，授权类型为password,参数必传", allowEmptyValue = true)
    private String password;

    @ApiModelProperty(value = "授权范围", required = true, example = "all")
    private String scope;

}
