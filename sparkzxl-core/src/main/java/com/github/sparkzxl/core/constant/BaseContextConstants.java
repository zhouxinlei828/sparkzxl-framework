package com.github.sparkzxl.core.constant;

/**
 * description: 常量工具类
 *
 * @author zhouxinlei
 */
public class BaseContextConstants {

    public static final String REMOTE_CALL = "remote_call";

    public static final String FALLBACK = "fallback";

    public static final String RESPONSE_RESULT_ANN = "response_result_ann";

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
     * resource 资源
     */
    public static final String AUTHORITY_PREFIX = "ROLE_";
    /**
     * token缓存前缀
     */
    public static final String CACHE_TOKEN_PREFIX = "TOKEN_";
    /**
     * 权限
     */
    public static final String AUTHORITY_NAME = "authorities";
    /**
     * JWT中封装的 用户id
     */
    public static final String JWT_KEY_USER_ID = "userid";
    /**
     * JWT中封装的 用户名称
     */
    public static final String JWT_KEY_NAME = "name";
    /**
     * JWT中封装的 用户账号
     */
    public static final String JWT_KEY_ACCOUNT = "account";
    /**
     * JWT中封装的 客户端id
     */
    public static final String CLIENT_ID = "client_id";
    /**
     * 租户标识
     */
    public static final String TENANT_ID = "tenantId";
    /**
     * 租户状态
     */
    public static final String TENANT_STATUS = "tenantStatus";
    /**
     * 刷新 Token
     */
    public static final String REFRESH_TOKEN_KEY = "refresh_token";
    /**
     * User信息 认证请求头前缀
     */
    public static final String BEARER_HEADER_PREFIX_EXT = "Bearer%20";
    /**
     * Client信息认证请求头前缀
     */
    public static final String BASIC_HEADER_PREFIX = "Basic ";
    /**
     * Client信息认证请求头前缀
     */
    public static final String BASIC_HEADER_PREFIX_EXT = "Basic%20";
    /**
     * 是否boot项目
     */
    public static final String IS_BOOT = "boot";
    /**
     * 日志链路追踪id信息头
     */
    public static final String TRACE_ID_HEADER = "x-trace-header";
    /**
     * 日志链路追踪id日志标志
     */
    public static final String LOG_TRACE_ID = "traceId";
    /**
     * 角色资源常量
     */
    public static final String RESOURCE_ROLES_MAP = "auth:resource_roles_map";
    public static final String VERSION = "version";
    /**
     * 登录账户 前缀
     * 完整key: authUser:{key} -> str
     */
    public static final String AUTH_USER = "login_user";
    /**
     * 登录账户token 前缀
     */
    public static final String AUTH_USER_TOKEN = "login_user_token";

    /**
     * 请求线程本地map
     */
    public static final String REQUEST_THREAD_LOCAL_MAP = "request-thread-local-map";

    /**
     * 客户端ip地址
     */
    public static final String CLIENT_IP = "clientIP";

    public static final String DATA_SCOPE = "data_scope";
    public static final String MULTI_DATA_SCOPE = "multiDataScope";
    public static final String RPC_TYPE = "rpc_type";
}
