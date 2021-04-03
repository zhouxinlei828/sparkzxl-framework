package com.github.sparkzxl.elasticsearch.exception;

import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import lombok.Getter;

/**
 * description: Elasticsearch异常类
 *
 * @author zhouxinlei
 */
public class ElasticsearchException extends RuntimeException {

    @Getter
    private int code;

    @Getter
    private String message;

    public ElasticsearchException(ApiResponseStatus resultCode) {
        this(resultCode.getCode(), resultCode.getMessage());
    }

    public ElasticsearchException(String message) {
        super(message);
    }

    public ElasticsearchException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public ElasticsearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public ElasticsearchException(Throwable cause) {
        super(cause);
    }

    public ElasticsearchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
