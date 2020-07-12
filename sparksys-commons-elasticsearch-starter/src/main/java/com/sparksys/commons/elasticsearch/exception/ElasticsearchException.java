package com.sparksys.commons.elasticsearch.exception;

import com.sparksys.commons.core.support.ResponseResultStatus;
import lombok.Getter;

/**
 * description: Elasticsearch异常类
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:18:09
 */
public class ElasticsearchException extends RuntimeException {

    @Getter
    private int code;

    @Getter
    private String message;

    public ElasticsearchException(ResponseResultStatus resultCode) {
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
