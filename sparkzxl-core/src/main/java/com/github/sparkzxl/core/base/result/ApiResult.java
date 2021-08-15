package com.github.sparkzxl.core.base.result;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * description: web接口响应结果
 *
 * @author zhouxinlei
 */
@Setter
@Getter
@Accessors(chain = true)
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 2887200772504212877L;

    private int code;
    private boolean success;
    private String msg;
    private T data;

    /**
     * 返回结果
     *
     * @param code 状态码
     * @param msg  信息
     * @return ApiResult
     */
    public static ApiResult<?> apiResult(int code, String msg) {
        return ApiResult.builder().code(code).msg(msg).build();
    }

    /**
     * 返回结果
     *
     * @param code 状态码
     * @param msg  信息
     * @param data 数据
     * @return ApiResult
     */
    public static <T> ApiResult<?> apiResult(int code, String msg, T data) {
        return ApiResult.builder().code(code).msg(msg).data(data).build();
    }

    /**
     * 返回结果
     *
     * @param resultStatus API操作码
     * @return ApiResult
     */
    public static <T> ApiResult<?> apiResult(ApiResponseStatus resultStatus) {
        return ApiResult.builder().code(resultStatus.getCode()).msg(resultStatus.getMessage()).build();
    }

    /**
     * 返回结果
     *
     * @param resultStatus API操作码
     * @param data         数据
     * @return ApiResult
     */
    public static <T> ApiResult<?> apiResult(ApiResponseStatus resultStatus, T data) {
        return ApiResult.builder().code(resultStatus.getCode()).msg(resultStatus.getMessage()).data(data).build();
    }

    /**
     * 超时返回结果
     *
     * @return ApiResult
     */
    public static <T> ApiResult<?> timeOut() {
        return ApiResult.apiResult(ApiResponseStatus.SERVICE_DEGRADATION);
    }

    public static <T> ApiResultBuilder<T> builder() {
        return new ApiResultBuilder<>();
    }


    public static class ApiResultBuilder<T> {
        private int code;
        private String msg;
        private T data;

        private ApiResultBuilder() {

        }

        public ApiResultBuilder<T> code(int code) {
            this.code = code;
            return this;
        }

        public ApiResultBuilder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }

        public ApiResultBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ApiResult<T> build() {
            return new ApiResult<T>()
                    .setCode(this.code)
                    .setSuccess(this.code == ApiResponseStatus.SUCCESS.getCode())
                    .setMsg(this.msg)
                    .setData(this.data);
        }
    }
}
