package com.github.sparkzxl.feign.default_;

import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.feign.exception.RemoteCallException;
import com.github.sparkzxl.model.exception.FeignErrorResult;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * description: 当调用远程服务 其抛出异常捕获
 *
 * @author zhouxinlei
 */
@Slf4j
public class FeignExceptionDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            Reader reader = response.body().asReader(StandardCharsets.UTF_8);
            String body = Util.toString(reader);
            FeignErrorResult feignErrorResult = JsonUtil.parse(body, FeignErrorResult.class);
            return new RemoteCallException(feignErrorResult.getMsg(), feignErrorResult.getExceptionChains());
        } catch (Exception e) {
            log.error("[{}] has an unknown exception.", methodKey, e);
            return new RemoteCallException("unKnowException", e);
        }

    }
}
