package com.github.sparkzxl.core.base.result;

import com.github.sparkzxl.core.assert_.BusinessEnumSysAssert;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description: 枚举一些常用API操作码
 *
 * @author zhouxinlei
 */
@Getter
@AllArgsConstructor
public enum ExceptionErrorCode implements BusinessEnumSysAssert {

    /**
     * 业务异常
     */
    FAILURE(ExceptionStatusConstant.SYSTEM_ERROR, "操作失败"),

    JSON_PARSE_ERROR("A0427", "JSON解析异常"),

    JSON_TRANSFORM_ERROR("A0428", "JSON转换异常"),

    NULL_POINTER_EXCEPTION_ERROR(ExceptionStatusConstant.SYSTEM_ERROR, "空指针异常"),

    CLUSTER_REDIRECT_EXCEPTION(ExceptionStatusConstant.SYSTEM_ERROR, "Redis集群异常"),

    CLUSTER_STATE_FAILURE_EXCEPTION(ExceptionStatusConstant.SYSTEM_ERROR, "Redis集群状态故障异常"),

    REDIS_CONNECTION_FAILURE_EXCEPTION(ExceptionStatusConstant.SYSTEM_ERROR, "Redis连接失败异常"),

    REDIS_SYSTEM_EXCEPTION(ExceptionStatusConstant.SYSTEM_ERROR, "Redis系统异常"),

    TOO_MANY_CLUSTER_REDIRECTIONS_EXCEPTION(ExceptionStatusConstant.SYSTEM_ERROR, "Redis集群重定向过多异常"),

    CLUSTER_COMMAND_EXECUTION_FAILURE_EXCEPTION(ExceptionStatusConstant.SYSTEM_ERROR, "Redis集群命令执行失败异常"),

    REDIS_SUBSCRIBED_CONNECTION_EXCEPTION(ExceptionStatusConstant.SYSTEM_ERROR, "Redis订阅连接异常"),

    /**
     * 未登录
     */
    UN_AUTHORIZED(ExceptionStatusConstant.HTTP_UNAUTHORIZED, "暂未登录或者token失效"),

    ACCOUNT_NOT_FOUND_EXCEPTION(ExceptionStatusConstant.SYSTEM_ERROR, "未知用户"),

    PASSWORD_EXCEPTION(ExceptionStatusConstant.SYSTEM_ERROR, "密码验证失败"),

    AUTHORIZED_FAIL(ExceptionStatusConstant.HTTP_UNAUTHORIZED, "授权失败，请重新尝试"),

    AUTHORIZED_DENIED(ExceptionStatusConstant.HTTP_FORBIDDEN, "无权限访问"),

    /**
     * 404 没找到请求
     */
    NOT_FOUND(ExceptionStatusConstant.HTTP_NOT_FOUND, "404 没找到请求"),

    /**
     * 消息不能读取
     */
    MSG_NOT_READABLE(ExceptionStatusConstant.HTTP_BAD_METHOD, "消息不能读取"),

    /**
     * 不支持当前请求方法
     */
    METHOD_NOT_SUPPORTED(ExceptionStatusConstant.HTTP_BAD_METHOD, "不支持当前请求方法"),

    /**
     * 不支持当前媒体类型
     */
    MEDIA_TYPE_NOT_SUPPORTED(ExceptionStatusConstant.HTTP_UNSUPPORTED_TYPE, "不支持当前媒体类型"),

    /**
     * 服务器异常
     */
    INTERNAL_SERVER_ERROR(ExceptionStatusConstant.HTTP_INTERNAL_ERROR, "系统繁忙，请稍候再试"),

    /**
     * 缺少必要的请求参数
     */
    PARAM_MISS(ExceptionStatusConstant.SYSTEM_ERROR, "缺少必要的请求参数"),

    /**
     * 请求参数类型错误
     */
    PARAM_TYPE_ERROR(ExceptionStatusConstant.SYSTEM_ERROR, "请求参数类型错误"),

    /**
     * 请求参数绑定错误
     */
    PARAM_BIND_ERROR(ExceptionStatusConstant.SYSTEM_ERROR, "请求参数绑定错误"),

    /**
     * 参数校验失败
     */
    PARAM_VALID_ERROR(ExceptionStatusConstant.SYSTEM_ERROR, "参数校验失败"),

    TOO_MUCH_DATA_ERROR(ExceptionStatusConstant.HTTP_INTERNAL_ERROR, "批量新增数据过多"),

    SERVICE_DEGRADATION(ExceptionStatusConstant.HTTP_UNAVAILABLE, "服务降级，请稍候再试"),

    /**
     * 请求被拒绝
     */
    REQ_REJECT(ExceptionStatusConstant.HTTP_FORBIDDEN, "请求被拒绝"),

    /**
     * 请求次数过多
     */
    REQ_LIMIT("1001", "单位时间内请求次数过多，请稍后再试"),

    /**
     * 黑名单
     */
    REQ_BLACKLIST("1002", "IP受限，请稍后再试"),

    /**
     * 请求次数过多
     */
    SYSTEM_BLOCK("1003", "系统负载过高，请稍后再试"),

    /**
     * 黑名单
     */
    PARAM_FLOW("1004", "热点参数访问频繁，请稍后再试"),

    USERNAME_EMPTY("1005", "用户名不能为空"),

    PASSWORD_EMPTY("1006", "密码不能为空"),

    PASSWORD_ERROR("1007", "密码不正确"),

    ACCOUNT_EMPTY("1008", "账户不存在"),

    UPLOAD_FAILURE("1009", "上传文件失败了哦"),


    /**
     * token已过期
     */
    TOKEN_EXPIRED_ERROR("A0230", "token已过期"),

    /**
     * token签名不合法
     */
    TOKEN_VALID_ERROR("2002", "token校验失败"),

    /**
     * token为空
     */
    JWT_EMPTY_ERROR("2003", "token为空"),
    /**
     * 未找到用户信息
     */
    NO_FOUND_USER("2004", "未找到用户信息"),

    TIME_OUT_ERROR(ExceptionStatusConstant.HTTP_INTERNAL_ERROR, "服务请求超时"),
    RETRY_ABLE_EXCEPTION(ExceptionStatusConstant.HTTP_INTERNAL_ERROR, "服务请求超时重试异常"),
    OPEN_SERVICE_UNAVAILABLE(ExceptionStatusConstant.HTTP_UNAVAILABLE, "【{}】服务不可用，请联系管理员！"),
    SQL_EX(ExceptionStatusConstant.SYSTEM_ERROR, "运行SQL出现异常"),
    SQL_MANY_RESULT_EX(ExceptionStatusConstant.SYSTEM_ERROR, "数据库查询出多条结果异常"),
    UNKNOWN_DATABASE(ExceptionStatusConstant.SYSTEM_ERROR, "数据库不存在，请联系管理员！"),
    UNKNOWN_TABLE(ExceptionStatusConstant.SYSTEM_ERROR, "表不存在，请联系管理员！"),
    UNKNOWN_COLUMN(ExceptionStatusConstant.SYSTEM_ERROR, "表字段不存在，请联系管理员！"),
    REQUIRED_FILE_PARAM_EX(ExceptionStatusConstant.SYSTEM_ERROR, "请求中必须至少包含一个有效文件"),
    ILLEGAL_ARGUMENT_EX(ExceptionStatusConstant.SYSTEM_ERROR, "无效参数异常"),
    PARAM_EX(ExceptionStatusConstant.SYSTEM_ERROR, "参数类型解析异常"),
    SIGNATURE_EX(ExceptionStatusConstant.SIGNATURE_EXCEPTION_STATUS, "签名异常"),
    REQUEST_TIMESTAMP_EX(ExceptionStatusConstant.SYSTEM_ERROR, "请求时间戳验签不通过"),
    SIGNATURE_NOT_SUPPORTED_EX(ExceptionStatusConstant.SYSTEM_ERROR, "不支持的验签请求"),
    //系统相关 end
    ;

    final String code;

    final String message;
}
