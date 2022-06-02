package com.github.sparkzxl.feign.decoder;

import com.github.sparkzxl.feign.entity.PredicateMessage;
import com.github.sparkzxl.feign.enums.FeignStatusEnum;
import com.github.sparkzxl.feign.exception.ExceptionDefinitionLocator;
import com.github.sparkzxl.feign.exception.ExceptionPredicate;
import com.github.sparkzxl.feign.exception.RemoteCallTransferException;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.HttpMessageConverterCustomizer;
import org.springframework.cloud.openfeign.support.SpringDecoder;

import java.lang.reflect.Type;
import java.util.function.Consumer;


/**
 * description: feign 请求解码器
 *
 * @author zhouxinlei
 * @since 2022-05-09 10:48:43
 */
public class FeignRequestDecoder extends SpringDecoder {

    private ExceptionDefinitionLocator exceptionDefinitionLocator;

    public FeignRequestDecoder(ObjectFactory<HttpMessageConverters> messageConverters,
                               ObjectProvider<HttpMessageConverterCustomizer> customizers) {
        super(messageConverters, customizers);
    }

    public FeignRequestDecoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        super(messageConverters, new EmptyObjectProvider<>());
    }

    public ExceptionDefinitionLocator getExceptionDefinitionLocator() {
        return exceptionDefinitionLocator;
    }

    public void setExceptionDefinitionLocator(ExceptionDefinitionLocator exceptionDefinitionLocator) {
        this.exceptionDefinitionLocator = exceptionDefinitionLocator;
    }

    @Override
    public Object decode(final Response response, Type type) throws FeignException {
        Object decode = null;
        try {
            decode = super.decode(response, type);
        } catch (Exception e) {
            if (ObjectUtils.isNotEmpty(exceptionDefinitionLocator)) {
                ExceptionPredicate<Response> exceptionPredicate = exceptionDefinitionLocator.getExceptionPredicate();
                boolean test = exceptionPredicate.test(response);
                if (!test) {
                    PredicateMessage predicateMessage = exceptionPredicate.getPredicateMessage();
                    throw new RemoteCallTransferException(FeignStatusEnum.TRANSFER_EXCEPTION.getCode(),
                            predicateMessage.getErrorCode(),
                            predicateMessage.getErrorMessage(),
                            response,
                            response.request());
                }
                throw new DecodeException(response.status(), "type is not an instance of Class or ParameterizedType: " + type,
                        response.request());
            }
        }
        return decode;
    }

    static class EmptyObjectProvider<T> implements ObjectProvider<T> {

        @Override
        public T getObject(Object... args) throws BeansException {
            return null;
        }

        @Override
        public T getIfAvailable() throws BeansException {
            return null;
        }

        @Override
        public T getIfUnique() throws BeansException {
            return null;
        }

        @Override
        public T getObject() throws BeansException {
            return null;
        }

        @Override
        public void forEach(Consumer action) {
            // do nothing
        }

    }

}
