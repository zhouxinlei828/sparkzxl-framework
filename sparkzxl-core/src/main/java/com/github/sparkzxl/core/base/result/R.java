package com.github.sparkzxl.core.base.result;

import cn.hutool.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.sparkzxl.core.base.HttpCode;
import com.github.sparkzxl.core.support.BaseException;
import com.github.sparkzxl.core.support.code.IErrorCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * description: web接口响应结果
 *
 * @author zhouxinlei
 */
@Setter
@Getter
@Accessors(chain = true)
public class R<T> implements Serializable {

    private static final long serialVersionUID = -720885758850999950L;

    /**
     * 响应编码
     */
    @JsonProperty(index = 1)
    private int code;

    /**
     * 响应信息
     */
    @JsonProperty(index = 2)
    private String message;

    /**
     * 是否成功
     */
    @JsonProperty(index = 3)
    private boolean success;

    /**
     * 响应数据
     */
    @JsonProperty(index = 4)
    private T result;

    /**
     * 异常码
     */
    @JsonProperty(index = 5)
    private String errorCode;

    /**
     * 异常信息
     */
    @JsonProperty(index = 6)
    private String errorMessage;

    /**
     * 附加数据
     */
    @JsonProperty(index = 8)
    private Map<String, Object> extra;

    public R(Integer code, String message, T result, IErrorCode errorCode) {
        this.code = code;
        this.message = message;
        this.success = (code == HttpStatus.HTTP_OK);
        this.result = result;
        this.errorCode = errorCode.getErrorCode();
        this.errorMessage = errorCode.getErrorMsg();
    }

    public R(Integer code, String message, T result) {
        this.code = code;
        this.message = message;
        this.success = (code == HttpStatus.HTTP_OK);
        this.result = result;
    }

    public R(HttpCode httpCode, T result, IErrorCode iBaseErrorCode) {
        this.code = httpCode.getCode();
        this.message = httpCode.getMessage();
        this.success = (code == HttpStatus.HTTP_OK);
        this.result = result;
        this.errorCode = iBaseErrorCode.getErrorCode();
        this.errorMessage = iBaseErrorCode.getErrorMsg();
    }

    public R(HttpCode httpCode, T result) {
        this.code = httpCode.getCode();
        this.message = httpCode.getMessage();
        this.success = (code == HttpStatus.HTTP_OK);
        this.result = result;
    }

    public R(HttpCode httpCode, IErrorCode iBaseErrorCode) {
        this.code = httpCode.getCode();
        this.message = httpCode.getMessage();
        this.success = (code == HttpStatus.HTTP_OK);
        this.errorCode = iBaseErrorCode.getErrorCode();
        this.errorMessage = iBaseErrorCode.getErrorMsg();
    }

    public R(HttpCode httpCode) {
        this.code = httpCode.getCode();
        this.message = httpCode.getMessage();
        this.success = (code == HttpStatus.HTTP_OK);
    }

    public R(HttpCode httpCode, String errorMessage) {
        this.code = httpCode.getCode();
        this.message = httpCode.getMessage();
        this.success = (code == HttpStatus.HTTP_OK);
        this.errorMessage = errorMessage;
    }

    public R(HttpCode httpCode, String errorCode, String errorMessage) {
        this.code = httpCode.getCode();
        this.message = httpCode.getMessage();
        this.success = (code == HttpStatus.HTTP_OK);
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public R(HttpCode httpCode, T result, String errorCode, String errorMessage) {
        this.code = httpCode.getCode();
        this.message = httpCode.getMessage();
        this.success = (code == HttpStatus.HTTP_OK);
        this.result = result;
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

    public static R success() {
        return new R<>(HttpCode.SUCCESS);
    }

    public static <T> R success(T data) {
        return new R<>(HttpCode.SUCCESS, data);
    }

    public static R fail() {
        return new R<>(HttpCode.FAILURE);
    }

    public static R failDetail(String errorCode, String errorMessage) {
        return new R<>(HttpCode.FAILURE, errorCode, errorMessage);
    }

    public static R fail(IErrorCode iBaseErrorCode) {
        return new R<>(HttpCode.FAILURE, iBaseErrorCode);
    }

    public static R fail(Integer code, String message) {
        return new R<>(code, message, null);
    }

    public static <T> R fail(HttpCode httpCode, T result) {
        return new R<>(httpCode, result);
    }

    public static R fail(HttpCode httpCode, IErrorCode iBaseErrorCode) {
        return new R<>(httpCode, iBaseErrorCode.getErrorCode(), iBaseErrorCode.getErrorMsg());
    }

    public static R fail(HttpCode httpCode, String errorCode, String errorMessage) {
        return new R<>(httpCode, errorCode, errorMessage);
    }

    public static <T> R fail(Integer code, String message, T data) {
        return new R<>(code, message, data);
    }

    public static R fail(BaseException baseException) {
        return new R<>(HttpCode.FAILURE, baseException.getErrorCode(), baseException.getErrorMsg());
    }

    public static R fail(IErrorCode iBaseErrorCode, String message) {
        return new R<>(HttpCode.FAILURE, iBaseErrorCode.getErrorCode(), message);
    }
}
