package com.sparksys.core.constant;

/**
 * description: 上下文常量工具类
 *
 * @author: zhouxinlei
 * @date: 2020-07-29 13:24:58
 */
public class BaseContextConstant {

    /**
     * JWT存储的请求头
     */
    public static final String JWT_TOKEN_HEADER = "Authorization";

    /**
     * JWT负载中拿到开头
     */
    public static final String BEARER_TOKEN = "Bearer ";

    public static final String BASIC_AUTH = "Basic";
    /**
     * 登录账户 前缀
     * 完整key: authUser:{key} -> str
     */
    public static String AUTH_USER = "login_user";

    /**
     * JWT中封装的 token 类型
     */
    public static final String JWT_KEY_TOKEN_TYPE = "token_type";

    /**
     * 当前线程请求用户的用户id
     */
    public static final String APPLICATION_AUTH_USER_ID = "current_request_userid";

    /**
     * 当前线程请求用户的用户名称
     */
    public static final String APPLICATION_AUTH_NAME = "current_request_name";

    /**
     * 当前线程请求用户的用户账号
     */
    public static final String APPLICATION_AUTH_ACCOUNT = "current_request_account";


    /**
     * resource 资源
     */
    public static final String AUTHORITY_PREFIX = "ROLE_";

    /**
     * 权限
     */
    public static final String AUTHORITY_CLAIM_NAME = "authorities";

}
