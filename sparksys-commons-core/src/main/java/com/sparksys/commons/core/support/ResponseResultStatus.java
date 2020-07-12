package com.sparksys.commons.core.support;

import cn.hutool.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description: 枚举一些常用API操作码
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:45:57
 */
@Getter
@AllArgsConstructor
public enum ResponseResultStatus implements BusinessEnumSysAssert {

    /**
     * 操作成功
     */
    SUCCESS(HttpStatus.HTTP_OK, "操作成功"),

    /**
     * 业务异常
     */
    FAILURE(HttpStatus.HTTP_BAD_REQUEST, "哎呀，开了个小差，请稍后再试"),

    /**
     * 请求未授权
     */
    UN_AUTHORIZED(HttpStatus.HTTP_UNAUTHORIZED, "暂未登录或token已过期"),

    AUTHORIZED_FAIL(HttpStatus.HTTP_UNAUTHORIZED, "授权失败，请重新尝试"),

    USERNAME_EMPTY(HttpStatus.HTTP_UNAUTHORIZED, "用户名不能为空"),

    PASSWORD_EMPTY(HttpStatus.HTTP_UNAUTHORIZED, "密码不能为空"),

    PASSWORD_ERROR(HttpStatus.HTTP_UNAUTHORIZED, "密码不正确"),

    ACCOUNT_EMPTY(HttpStatus.HTTP_UNAUTHORIZED, "账户不存在"),

    UN_PERMISSION(HttpStatus.HTTP_UNAUTHORIZED, "抱歉，您没有访问权限"),
    /**
     * 404 没找到请求
     */
    NOT_FOUND(HttpStatus.HTTP_NOT_FOUND, "404 没找到请求"),

    /**
     * 消息不能读取
     */
    MSG_NOT_READABLE(HttpStatus.HTTP_BAD_REQUEST, "消息不能读取"),

    /**
     * 不支持当前请求方法
     */
    METHOD_NOT_SUPPORTED(HttpStatus.HTTP_BAD_METHOD, "不支持当前请求方法"),

    /**
     * 不支持当前媒体类型
     */
    MEDIA_TYPE_NOT_SUPPORTED(HttpStatus.HTTP_UNSUPPORTED_TYPE, "不支持当前媒体类型"),

    /**
     * 服务器异常
     */
    INTERNAL_SERVER_ERROR(HttpStatus.HTTP_INTERNAL_ERROR, "系统繁忙，请稍候再试"),

    /**
     * 缺少必要的请求参数
     */
    PARAM_MISS(HttpStatus.HTTP_BAD_REQUEST, "缺少必要的请求参数"),

    /**
     * 请求参数类型错误
     */
    PARAM_TYPE_ERROR(HttpStatus.HTTP_BAD_REQUEST, "请求参数类型错误"),

    /**
     * 请求参数绑定错误
     */
    PARAM_BIND_ERROR(HttpStatus.HTTP_BAD_REQUEST, "请求参数绑定错误"),

    /**
     * 参数校验失败
     */
    PARAM_VALID_ERROR(HttpStatus.HTTP_BAD_REQUEST, "参数校验失败"),

    MUCH_KILL(HttpStatus.HTTP_INTERNAL_ERROR, "哎呦喂，人也太多了，请稍后！"),

    SUCCESS_KILL(HttpStatus.HTTP_OK, "秒杀成功"),

    END_KILL(HttpStatus.HTTP_BAD_REQUEST, "秒杀结束"),

    TOO_MUCH_DATA_ERROR(HttpStatus.HTTP_INTERNAL_ERROR, "批量新增数据过多"),

    SERVICE_MAPPER_ERROR(-11, "Mapper类转换异常"),

    SERVICE_DEGRADATION(HttpStatus.HTTP_UNAVAILABLE, "服务降级，请稍候再试"),

    UPLOAD_FAILURE(HttpStatus.HTTP_INTERNAL_ERROR, "上传文件失败了哦"),

    /**
     * 请求被拒绝
     */
    REQ_REJECT(HttpStatus.HTTP_FORBIDDEN, "请求被拒绝"),

    /**
     * 请求次数过多
     */
    REQ_LIMIT(1001, "单位时间内请求次数过多，请稍后再试"),

    /**
     * 黑名单
     */
    REQ_BLACKLIST(1002, "IP已被禁止，请稍后再试"),

    /**
     * 黑名单
     */
    PARAM_FLOW(1003, "热点参数访问限制，请稍后再试");

    final int code;

    final String message;
}
