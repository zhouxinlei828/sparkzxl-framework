package com.github.sparkzxl.feign.decoder;

import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.entity.response.Response;
import com.github.sparkzxl.feign.enums.FeignStatusEnum;
import com.github.sparkzxl.feign.exception.RemoteCallTransferException;
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
    public Exception decode(String methodKey, feign.Response response) {
        try {
            Reader reader = response.body().asReader(StandardCharsets.UTF_8);
            String body = Util.toString(reader);
            Response<?> responseResult = JsonUtil.parse(body, Response.class);
            return new RemoteCallTransferException(FeignStatusEnum.TRANSFER_EXCEPTION.getCode(), responseResult.getErrorCode(), responseResult.getErrorMsg(), response.request());
        } catch (Exception e) {
            log.error("[{}] has an unknown exception.", methodKey, e);
            return new RemoteCallTransferException(FeignStatusEnum.UNKNOWN_EXCEPTION.getCode(), ExceptionErrorCode.FAILURE.getErrorCode(), "unKnowException", e, response.request());
        }

    }
}
