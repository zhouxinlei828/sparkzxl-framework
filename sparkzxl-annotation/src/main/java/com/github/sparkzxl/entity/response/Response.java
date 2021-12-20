package com.github.sparkzxl.entity.response;

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

    private static final long serialVersionUID = 6114350052238067773L;

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
    public static Response<?> fail(int code, String msg) {
        return Response.builder().code(code).msg(msg).build();
    }

    /**
     * 返回结果
     *
     * @param code 状态码
     * @param msg  信息
     * @return ApiResult
     */
    public static <T> Response<?> fail(int code, String msg, T data) {
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
    public static <T> Response<?> success(int code, String msg, T data) {
        return Response.builder().code(code).msg(msg).data(data).build();
    }

    /**
     * 返回结果
     *
     * @param data 数据
     * @return ApiResult
     */
    public static <T> Response<?> success(T data) {
        return Response.builder().code(200).msg("成功").data(data).build();
    }


    public static <T> ResponseBuilder<T> builder() {
        return new ResponseBuilder<>();
    }


    public static class ResponseBuilder<T> {
        private int code;
        private String msg;
        private T data;

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

        public Response<T> build() {
            return new Response<T>()
                    .setCode(this.code)
                    .setSuccess(this.code == 200)
                    .setMsg(this.msg)
                    .setData(this.data);
        }
    }
}
