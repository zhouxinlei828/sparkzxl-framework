package com.github.sparkzxl.feign.default_;

import cn.hutool.http.HttpStatus;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.constant.ExceptionConstant;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.utils.ResponseResultUtils;
import com.github.sparkzxl.feign.config.FeignExceptionHandlerContext;
import com.github.sparkzxl.feign.exception.RemoteCallException;
import com.github.sparkzxl.model.exception.ExceptionChain;
import com.github.sparkzxl.model.exception.FeignErrorResult;
import com.google.common.collect.Maps;
import io.seata.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.*;

/**
 * description: 当服务内报错 返回给Feign的处理器
 *
 * @author zhouxinlei
 */
@Slf4j
public class FeignErrorHandler extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(webRequest, options);
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
        Integer status = (Integer) errorAttributes.get("status");
        String message = error.getCause().getMessage();
        ResponseResultUtils.clearResponseResult();
        // 判断是否是feign请求
        String feign = webRequest.getHeader(BaseContextConstants.REMOTE_CALL);
        if (StringUtils.isNotEmpty(feign)) {
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
            return feignResponse(status, message, exceptionChains);
        }
        return response(status, message);
    }


    /**
     * 构建返回的JSON数据格式
     *
     * @param status          状态码
     * @param errorMessage    异常信息
     * @param exceptionChains 异常链实
     * @return Map<String, Object>
     */
    public static Map<String, Object> feignResponse(int status, String errorMessage, List<ExceptionChain> exceptionChains) {
        FeignErrorResult feignErrorResult = FeignErrorResult.feignErrorResult(status, errorMessage, exceptionChains);
        log.error("feign 请求拦截异常：[{}]", JsonUtil.toJson(feignErrorResult));
        return JsonUtil.toMap(feignErrorResult);
    }

    /**
     * 构建返回的JSON数据格式
     *
     * @param status       状态码
     * @param errorMessage 异常信息
     * @return Map<String, Object>
     */
    public static Map<String, Object> response(int status, String errorMessage) {
        Map<String, Object> responseMap = Maps.newLinkedHashMap();
        responseMap.put("code", status);
        responseMap.put("success", status == HttpStatus.HTTP_OK);
        responseMap.put("msg", errorMessage);
        responseMap.put("data", null);
        log.error("正常请求拦截异常：[{}]", JsonUtil.toJson(responseMap));
        return responseMap;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 14;
    }
}

