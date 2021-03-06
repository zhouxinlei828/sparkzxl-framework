package com.github.sparkzxl.feign.default_;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import com.github.sparkzxl.feign.config.FeignExceptionHandlerContext;
import com.github.sparkzxl.feign.constant.ExceptionConstant;
import com.github.sparkzxl.feign.exception.RemoteCallException;
import com.github.sparkzxl.feign.model.ExceptionChain;
import com.github.sparkzxl.core.annotation.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * description: 当服务内报错 返回给Feign的处理器
 *
 * @author: zhouxinlei
 * @date: 2020-12-08 11:39:12
 */
@Slf4j
public class FeignExceptionHandler extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = this.getErrorAttributes(webRequest, options.isIncluded(ErrorAttributeOptions.Include.STACK_TRACE));
        Throwable error = getError(webRequest);
        List<ExceptionChain> exceptionChains = null;
        if (error instanceof RemoteCallException) {
            exceptionChains = ((RemoteCallException) error).getExceptionChains();
        } else {
            Object attribute = webRequest.getAttribute(ExceptionConstant.EXCEPTION_CHAIN_KEY, RequestAttributes.SCOPE_REQUEST);
            if (attribute != null) {
                exceptionChains = JSON.parseArray(attribute.toString(), ExceptionChain.class);
            }
            if (exceptionChains == null) {
                exceptionChains = new ArrayList<>(1);
            }
        }

        ExceptionChain exceptionChain = new ExceptionChain();
        exceptionChain.setMsg(error.getMessage());
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
        String message = (String) errorAttributes.get("message");
        errorAttributes.put("code", status);
        errorAttributes.put("success", false);
        errorAttributes.put("msg", message);
        errorAttributes.remove("status");
        errorAttributes.remove("message");
        handleResponseResult();
        return errorAttributes;
    }

    public void handleResponseResult() {
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        ResponseResult responseResult = (ResponseResult) servletRequest.getAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        boolean result = responseResult != null;
        if (result) {
            servletRequest.removeAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        }
    }


}

