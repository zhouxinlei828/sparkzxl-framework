package com.github.sparkzxl.gateway.plugin.exception.strategy;

import cn.hutool.core.bean.OptionalBean;
import cn.hutool.core.text.StrFormatter;
import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.entity.response.Response;
import com.github.sparkzxl.gateway.plugin.exception.result.ExceptionHandlerResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;

/**
 * description: 服务器资源未找到异常处理
 *
 * @author zhoux
 */
@Slf4j
public class NotFoundExceptionHandlerStrategy implements ExceptionHandlerStrategy<NotFoundException> {

    @Override
    public Class getHandleClass() {
        return NotFoundException.class;
    }

    @Override
    public ExceptionHandlerResult handleException(Throwable throwable) {
        NotFoundException e = (NotFoundException) throwable;
        log.error("NotFoundException：[{}]", e.getReason());
        String matchString = "Unable to find instance for ";
        String message = e.getReason();
        assert message != null;
        if (message.contains(matchString)) {
            int indexOf = message.lastIndexOf("for ") + 4;
            String serviceName = message.substring(indexOf);
            String applicationName = OptionalBean.ofNullable(serviceName).orElseGet(() -> "unKnownServer");
            message = StrFormatter.format(ExceptionErrorCode.OPEN_SERVICE_UNAVAILABLE.getMessage(), applicationName);
        }
        Response responseResult = Response.failDetail(ExceptionErrorCode.OPEN_SERVICE_UNAVAILABLE.getCode(), message);
        String response = JSON.toJSONString(responseResult);
        ExceptionHandlerResult result = new ExceptionHandlerResult(HttpStatus.NOT_FOUND, response);
        log.debug("Handle NotFoundException:{},Result:{}", throwable.getMessage(), result);
        return result;
    }
}
