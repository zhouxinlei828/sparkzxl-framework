package com.github.sparkzxl.feign.exception;

import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.feign.entity.PredicateMessage;
import feign.Response;
import feign.Util;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * description: 正常响应异常谓词工厂
 *
 * @author zhouxinlei
 * @since 2022-05-09 15:04:42
 */
public class NormalRespExceptionPredicateFactory extends AbstractExceptionPredicateFactory<NormalRespExceptionPredicateFactory.Config> {

    public NormalRespExceptionPredicateFactory() {
        super(Config.class);
    }

    @Override
    public ExceptionPredicate<Response> apply(Config config) {
        return new FeignExceptionPredicate() {
            @Override
            public boolean test(Response response) {
                try {
                    Reader reader = response.body().asReader(StandardCharsets.UTF_8);
                    String body = Util.toString(reader);
                    if (StringUtils.isNotEmpty(body)) {
                        com.github.sparkzxl.entity.response.Response<?> responseResult = JsonUtil.parse(body, com.github.sparkzxl.entity.response.Response.class);
                        if (!responseResult.isSuccess()) {
                            setPredicateMessage(PredicateMessage.convert(responseResult.getErrorCode(), responseResult.getErrorMsg()));
                        }
                        return responseResult.isSuccess() == config.getOk();
                    }
                    setPredicateMessage(PredicateMessage.convert(ExceptionErrorCode.DECODE_EXCEPTION.getErrorCode(), ExceptionErrorCode.DECODE_EXCEPTION.getErrorMessage()));
                    return false;
                } catch (IOException e) {
                    return false;
                }
            }
        };
    }

    public static class Config {

        private Boolean ok;

        public Boolean getOk() {
            return ok;
        }

        public void setOk(Boolean ok) {
            this.ok = ok;
        }
    }
}
