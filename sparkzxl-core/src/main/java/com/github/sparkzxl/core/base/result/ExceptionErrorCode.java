package com.github.sparkzxl.core.base.result;

import com.github.sparkzxl.entity.response.IErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description: 枚举一些常用API操作码
 *
 * @author zhouxinlei
 */
@Getter
@AllArgsConstructor
public enum ExceptionErrorCode implements IErrorCode {

    // 消息不能读取
    MSG_NOT_READABLE("405", "消息不能读取"),
    METHOD_NOT_SUPPORTED("405", "不支持当前请求方法"),
    MEDIA_TYPE_NOT_SUPPORTED("415", "不支持当前媒体类型"),

    // 文件上传异常
    FILE_UPLOAD_EX("A0700", "用户上传文件异常"),
    SIGNATURE_EX("A0340", "签名异常"),
    REQUEST_TIMESTAMP_EX("A0414", "请求时间戳验签不通过"),
    JSON_PARSE_ERROR("A0427", "JSON解析异常"),
    JSON_TRANSFORM_ERROR("A0428", "JSON转换异常"),
    LOGIN_EXPIRE("A0230", "用户登陆已过期"),
    PASSWORD_EXCEPTION("A0120", "密码验证失败"),
    AUTHORIZED_DENIED("A0312", "无权限使用 API"),
    PARAM_MISS("A0410", "请求必填参数为空"),
    PARAM_TYPE_ERROR("A0421", "参数格式不匹配"),
    PARAM_VALID_ERROR("A0415", "参数校验失败"),
    PARAM_EX("A0416", "参数类型解析异常"),
    TOO_MUCH_DATA_ERROR("A0426", "请求批量处理总个数超出限制"),
    REQ_BLACKLIST("A0323", "非法 IP 地址"),
    PARAM_FLOW("A0321", "热点参数访问频繁，请稍后再试"),
    USERNAME_EMPTY("A0110", "用户名不能为空"),
    PASSWORD_EMPTY("A0120", "密码不能为空"),
    TOKEN_VALID_ERROR("A0311", "token校验失败"),
    USER_IDENTITY_VERIFICATION_ERROR("A0220", "用户身份校验失败"),
    USER_NOT_FOUND("A0201", "用户账户不存在"),


    //系统相关 start
    FAILURE("B0001", "操作失败"),
    NULL_POINTER_EXCEPTION_ERROR("B0001", "空指针异常"),
    INTERNAL_SERVER_ERROR("B0001", "系统执行出错"),
    TIME_OUT_ERROR("B0100", "服务请求超时"),
    RETRY_ABLE_EXCEPTION("B0100", "服务请求超时重试异常"),
    OPEN_SERVICE_UNAVAILABLE("B0200", "【{}】服务不可用，请联系管理员！"),
    SYSTEM_BLOCK("B0310", "系统负载过高，请稍后再试"),
    SERVICE_DEGRADATION("B0220", "系统功能降级"),
    REQ_LIMIT("B0210", "系统限流，请稍后再试"),
    //系统相关 end

    // 接口不存在
    NOT_FOUND("C0113", "接口不存在"),
    SQL_EX("C0300", "运行SQL出现异常"),
    SQL_MANY_RESULT_EX("C0300", "数据库查询出多条结果异常"),
    UNKNOWN_DATABASE("C0300", "数据库不存在，请联系管理员！"),
    UNKNOWN_TABLE("C0311", "表不存在，请联系管理员！"),
    UNKNOWN_COLUMN("C0312", "字段不存在，请联系管理员！"),
    CLUSTER_REDIRECT_EXCEPTION("C0230", "Redis集群异常"),
    CLUSTER_STATE_FAILURE_EXCEPTION("C0230", "Redis集群状态故障异常"),
    REDIS_CONNECTION_FAILURE_EXCEPTION("C0230", "Redis连接失败异常"),
    REDIS_SYSTEM_EXCEPTION("C0230", "Redis系统异常"),
    TOO_MANY_CLUSTER_REDIRECTIONS_EXCEPTION("C0230", "Redis集群重定向过多异常"),
    CLUSTER_COMMAND_EXECUTION_FAILURE_EXCEPTION("C0230", "Redis集群命令执行失败异常"),
    REDIS_SUBSCRIBED_CONNECTION_EXCEPTION("C0230", "Redis订阅连接异常"),

    ;

    final String errorCode;

    final String errorMessage;
}
