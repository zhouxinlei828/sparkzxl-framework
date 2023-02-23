package com.github.sparkzxl.alarm.entity;

import com.github.sparkzxl.alarm.enums.AlarmErrorEnum;

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


    public AlarmResponse(String code, String message, String logId, String data) {
        this.code = code;
        this.message = message;
        this.logId = logId;
        this.data = data;
    }

    public static <T> AlarmResponse success(String logId, String data) {
        return new AlarmResponse(AlarmErrorEnum.SUCCESS.getErrorCode(), AlarmErrorEnum.SUCCESS.getErrorMsg(), logId, data);
    }


    public static AlarmResponse failed(String logId) {
        return new AlarmResponse(AlarmErrorEnum.FAILED.getErrorCode(), AlarmErrorEnum.FAILED.getErrorMsg(), logId, null);
    }

    public static AlarmResponse failed(String logId, String errorMsg) {
        return new AlarmResponse(AlarmErrorEnum.FAILED.getErrorCode(), errorMsg, logId, null);
    }

    public static AlarmResponse failed(String logId, AlarmErrorEnum resultCode) {
        return new AlarmResponse(resultCode.getErrorCode(), resultCode.getErrorMsg(), logId, null);
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