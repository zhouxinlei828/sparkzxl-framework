package com.github.sparkzxl.core.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * description:JWT中存储的信息
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 07:51:20
 */
@Data
public class JwtUserInfo<T> implements Serializable {

    private static final long serialVersionUID = 1975831592308179065L;

    private T id;
    /**
     * 用户名
     */
    private String username;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * clientId
     */
    private String clientId;
    /**
     * 主题
     */
    private String sub;
    /**
     * 签发时间
     */
    private Long iat;
    /**
     * 过期时间
     */
    private Date expire;
    /**
     * JWT的ID
     */
    private String jti;
    /**
     * 用户拥有的权限
     */
    private List<String> authorities;

    private Integer exp;

    @JsonProperty("auth_time")
    private Integer authTime;

    private String iss;

    private String aud;

    private String typ;

    private String azp;

    private String nonce;

    @JsonProperty("session_state")
    private String sessionState;

    private String acr;

    @JsonProperty("realm_access")
    private RealmAccess realmAccess;

    @JsonProperty("resource_access")
    private ResourceAccess resourceAccess;

    private String scope;

    private String upn;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    private Address address;

    @JsonProperty("preferred_username")
    private String preferredUsername;

    @JsonProperty("given_name")

    private String givenName;
    @JsonProperty("family_name")
    private String familyName;

    private String email;

    @JsonProperty("allowed-origins")
    private List<String> allowedOrigins;

    private List<String> groups;

}
