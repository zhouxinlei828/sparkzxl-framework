package com.github.sparkzxl.core.base.result;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = 6322130956803761582L;

    @JsonProperty(index = 1)
    private int code;
    @JsonProperty(index = 2)
    private boolean success;
    @JsonProperty(index = 3)
    private String msg;
    @JsonProperty(index = 4)
    private T data;

    /**
     * 返回结果
     *
     * @param code 状态码
     * @param msg  信息
     * @return ApiResult
     */
    public static ResponseResult<?> result(int code, String msg) {
        return ResponseResult.builder().code(code).msg(msg).build();
    }

    /**
     * 返回结果
     *
     * @param code 状态码
     * @param msg  信息
     * @param data 数据
     * @return ApiResult
     */
    public static <T> ResponseResult<?> result(int code, String msg, T data) {
        return ResponseResult.builder().code(code).msg(msg).data(data).build();
    }

    /**
     * 返回结果
     *
     * @param resultStatus API操作码
     * @return ApiResult
     */
    public static ResponseResult<?> result(ResponseInfoStatus resultStatus) {
        return ResponseResult.builder().code(resultStatus.getCode()).msg(resultStatus.getMessage()).build();
    }

    /**
     * 返回结果
     *
     * @param resultStatus API操作码
     * @param data         数据
     * @return ApiResult
     */
    public static <T> ResponseResult<?> result(ResponseInfoStatus resultStatus, T data) {
        return ResponseResult.builder().code(resultStatus.getCode()).msg(resultStatus.getMessage()).data(data).build();
    }

    /**
     * 超时返回结果
     *
     * @return ApiResult
     */
    public static <T> ResponseResult<?> timeOut() {
        return ResponseResult.result(ResponseInfoStatus.SERVICE_DEGRADATION);
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

        public ResponseResult<T> build() {
            return new ResponseResult<T>()
                    .setCode(this.code)
                    .setSuccess(this.code == ResponseInfoStatus.SUCCESS.getCode())
                    .setMsg(this.msg)
                    .setData(this.data);
        }
    }
}
