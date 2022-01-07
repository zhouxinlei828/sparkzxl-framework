package com.github.sparkzxl.entity.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * description: web接口响应结果
 *
 * @author zhouxinlei
 */
@Setter
@Getter
@Accessors(chain = true)
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 6114350052238067773L;

    private static final String SUCCESS_CODE = "200";

    @JsonProperty(index = 1)
    private String code;

    @JsonProperty(index = 2)
    private boolean success;

    @JsonProperty(index = 3)
    private String msg;

    @JsonProperty(index = 4)
    private String requestId;

    @JsonProperty(index = 5)
    private T data;

    /**
     * 返回结果
     *
     * @param code 状态码
     * @param msg  信息
     * @return ApiResult
     */
    public static Response<?> fail(String code, String msg) {
        return Response.builder().code(code).msg(msg).build();
    }

    /**
     * 返回结果
     *
     * @param code 状态码
     * @param msg  信息
     * @return ApiResult
     */
    public static <T> Response<?> fail(String code, String msg, T data) {
        return Response.builder().code(code).msg(msg).data(data).build();
    }

    /**
     * 返回结果
     *
     * @param code 状态码
     * @param msg  信息
     * @param data 数据
     * @return ApiResult
     */
    public static <T> Response<?> success(String code, String msg, T data) {
        return Response.builder().code(code).msg(msg).data(data).build();
    }

    /**
     * 返回结果
     *
     * @param data 数据
     * @return ApiResult
     */
    public static <T> Response<?> success(T data) {
        return Response.builder().code(SUCCESS_CODE).msg("成功").data(data).build();
    }


    public static <T> ResponseBuilder<T> builder() {
        return new ResponseBuilder<>();
    }


    public static class ResponseBuilder<T> {
        private String code;
        private String msg;
        private T data;
        private String requestId;

        private ResponseBuilder() {

        }

        public ResponseBuilder<T> code(String code) {
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

        public Response<T> build() {
            return new Response<T>()
                    .setCode(this.code)
                    .setSuccess(Objects.equals(this.code, SUCCESS_CODE))
                    .setMsg(this.msg)
                    .setData(this.data)
                    .setRequestId(this.requestId);
        }
    }
}
