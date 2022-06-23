package com.github.sparkzxl.entity.response;

import cn.hutool.http.HttpStatus;
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
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1648240637156580226L;

    @JsonProperty(index = 1)
    private int code;

    @JsonProperty(index = 2)
    private String msg;

    @JsonProperty(index = 3)
    private boolean success;

    @JsonProperty(index = 4)
    private T data;

    @JsonProperty(index = 5)
    private String errorCode;

    @JsonProperty(index = 6)
    private String errorMsg;

    @JsonProperty(index = 7)
    private String requestId;

    /**
     * 异常返回结果
     *
     * @param errorCode 异常状态码
     * @param errorMsg  异常信息
     * @return Response
     */
    public static Response<?> failDetail(String errorCode, String errorMsg) {
        return response(ResponseCode.FAILURE.getCode(), ResponseCode.FAILURE.getMessage(), null, errorCode, errorMsg);
    }

    /**
     * 异常返回结果
     *
     * @param errorCode 异常状态
     * @return Response
     */
    public static Response<?> failDetail(IErrorCode errorCode) {
        return response(ResponseCode.FAILURE.getCode(), ResponseCode.FAILURE.getMessage(), null, errorCode.getErrorCode(), errorCode.getErrorMessage());
    }

    /**
     * 返回结果
     *
     * @param errorCode 异常状态码
     * @param errorMsg  异常信息
     * @return Response
     */
    public static <T> Response<?> response(int code, String msg, T data, String errorCode, String errorMsg) {
        return Response.builder()
                .code(code)
                .msg(msg)
                .data(data)
                .errorCode(errorCode)
                .errorMsg(errorMsg)
                .build();
    }

    /**
     * 成功返回结果
     *
     * @param data 数据
     * @return Response
     */
    public static <T> Response<?> success(T data) {
        return response(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data, null, null);
    }

    public static <T> ResponseBuilder<T> builder() {
        return new ResponseBuilder<>();
    }


    public static class ResponseBuilder<T> {
        private int code;
        private String msg;
        private T data;
        private String errorCode;
        private String errorMsg;
        private String requestId;

        private ResponseBuilder() {

        }

        public ResponseBuilder<T> code(int code) {
            this.code = code;
            return this;
        }

        public ResponseBuilder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }

        public ResponseBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ResponseBuilder<T> requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public ResponseBuilder<T> errorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public ResponseBuilder<T> errorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }

        public Response<T> build() {
            return new Response<T>()
                    .setCode(this.code)
                    .setSuccess(this.code == HttpStatus.HTTP_OK)
                    .setMsg(this.msg)
                    .setData(this.data)
                    .setErrorCode(this.errorCode)
                    .setErrorMsg(this.errorMsg)
                    .setRequestId(this.requestId);
        }
    }
}
