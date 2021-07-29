package com.github.sparkzxl.feign.default_;

import com.github.sparkzxl.constant.ExceptionConstant;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.utils.ResponseResultUtils;
import com.github.sparkzxl.feign.config.FeignExceptionHandlerContext;
import com.github.sparkzxl.feign.exception.RemoteCallException;
import com.github.sparkzxl.model.exception.ExceptionChain;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * description: 当服务内报错 返回给Feign的处理器
 *
 * @author zhouxinlei
 */
@Slf4j
public class FeignExceptionHandler extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, boolean includeStackTrace) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, includeStackTrace);
        Throwable error = super.getError(webRequest);
        List<ExceptionChain> exceptionChains = null;
        if (error instanceof RemoteCallException) {
            exceptionChains = ((RemoteCallException) error).getExceptionChains();
        } else {
            Object attribute = webRequest.getAttribute(ExceptionConstant.EXCEPTION_CHAIN_KEY, RequestAttributes.SCOPE_REQUEST);
            if (attribute != null) {
                exceptionChains = JsonUtil.parseArray(attribute.toString(), ExceptionChain.class);
            }
            if (exceptionChains == null) {
                exceptionChains = new ArrayList<>(1);
            }
        }
        String message = error.getCause().getMessage();
        ExceptionChain exceptionChain = new ExceptionChain();
        exceptionChain.setMsg(message);
        exceptionChain.setPath(errorAttributes.get("path").toString());
        exceptionChain.setTimestamp(new Date());
        exceptionChain.setApplicationName(FeignExceptionHandlerContext.getApplicationName());
        //添加发生的异常类信息 以便下一步处理
        if (error.getClass() != null) {
            exceptionChain.setExceptionClass(error.getClass().getTypeName());
        }
        exceptionChains.add(exceptionChain);
        errorAttributes.put(ExceptionConstant.EXCEPTION_CHAIN_KEY, exceptionChains);
        Integer status = (Integer) errorAttributes.get("status");
        ResponseResultUtils.clearResponseResult();
        return response(status, message);
    }


    /**
     * 构建返回的JSON数据格式
     *
     * @param status       状态码
     * @param errorMessage 异常信息
     * @return Map<String, Object>
     */
    public static Map<String, Object> response(int status, String errorMessage) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("code", status);
        map.put("msg", errorMessage);
        map.put("data", null);
        map.put("success", status == 200);
        log.error(map.toString());
        return map;
    }
}

