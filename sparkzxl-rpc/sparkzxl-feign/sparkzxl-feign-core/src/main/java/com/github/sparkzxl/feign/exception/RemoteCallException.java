package com.github.sparkzxl.feign.exception;

import feign.FeignException;
import feign.Response;
import feign.Util;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * description: feign远程调用异常
 *
 * @author zhouxinlei
 */
@Slf4j
@Getter
@Setter
@ToString
public class RemoteCallException extends FeignException {

    private Response response;
    private String body;

    public RemoteCallException(int status, String message, Response response) {
        super(status, message);
        this.response = response;
        try {
            Reader reader = response.body().asReader(StandardCharsets.UTF_8);
            this.body = Util.toString(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
