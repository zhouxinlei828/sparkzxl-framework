package com.github.sparkzxl.alarm.entity;

import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import com.github.sparkzxl.core.support.code.IErrorCode;

/**
 * description: 告警响应体
 *
 * @author zhouxinlei
 * @since 2022-05-18 10:52:42
 */
public class AlarmResponse {
    /**
     * 响应码
     */
    private String code;
    /**
     * 响应消息
     */
    private String message;
    /**
     * 日志id
     */
    private String logId;
    /**
     * 响应数据
     */
    private String data;


    private AlarmResponse(IErrorCode resultCode) {
        this.code = resultCode.getErrorCode();
        this.message = resultCode.getErrorMsg();
    }

    private AlarmResponse(String logId, AlarmResponseCodeEnum resultCode) {
        this(resultCode);
        this.logId = logId;
    }

    private AlarmResponse(AlarmResponseCodeEnum resultCode, String data) {
        this(resultCode);
        this.data = data;
    }

    private AlarmResponse(AlarmResponseCodeEnum resultCode, String logid, String data) {
        this(logid, resultCode);
        this.data = data;
    }

    public static <T> AlarmResponse success(String logId, String data) {
        return new AlarmResponse(AlarmResponseCodeEnum.SUCCESS, logId, data);
    }

    public static <T> AlarmResponse success(AlarmResponseCodeEnum resultCode, String logId, String data) {
        return new AlarmResponse(resultCode, logId, data);
    }

    public static AlarmResponse failed(String logId) {
        return new AlarmResponse(logId, AlarmResponseCodeEnum.FAILED);
    }

    public static AlarmResponse failed(String logId, AlarmResponseCodeEnum resultCode) {
        return new AlarmResponse(logId, resultCode);
    }

    public static AlarmResponse failed(AlarmResponseCodeEnum resultCode, String data) {
        return new AlarmResponse(resultCode, data);
    }

    public static AlarmResponse failed(AlarmResponseCodeEnum resultCode) {
        return new AlarmResponse(resultCode);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("[code=%s, message=%s, logId=%s, data=%s]",
                code, message, logId, data);
    }
}