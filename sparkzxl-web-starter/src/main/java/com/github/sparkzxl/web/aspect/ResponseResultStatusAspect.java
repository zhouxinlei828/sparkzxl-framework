package com.github.sparkzxl.web.aspect;

import cn.hutool.core.convert.Convert;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.annotation.result.ResponseResult;
import com.github.sparkzxl.constant.AppContextConstants;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * description: 自定义处理响应状态切面
 *
 * @author zhouxinlei
 */
@Aspect
@Slf4j
public class ResponseResultStatusAspect {

    private boolean transferExceptionStatus = false;

    private boolean enableTransferStatus = false;

    public void setTransferExceptionStatus(boolean transferExceptionStatus) {
        this.transferExceptionStatus = transferExceptionStatus;
    }

    public void setEnableTransferStatus(boolean enableTransferStatus) {
        this.enableTransferStatus = enableTransferStatus;
    }

    @Pointcut("@within(com.github.sparkzxl.annotation.ResponseResultStatus)||@annotation(com.github.sparkzxl.annotation.ResponseResultStatus)")
    public void pointCut() {

    }

    /**
     * 对Controller下面的方法执行前进行切入，初始化开始时间
     */
    @Before(value = "pointCut()")
    public void beforeMethod() {
        if (log.isDebugEnabled()) {
            log.debug("设置响应状态切面开始,是否内部服务传递异常：{}", transferExceptionStatus);
        }
    }

    /**
     * 环绕操作
     *
     * @param proceedingJoinPoint 切入点
     * @return 原方法返回值
     * @throws Throwable 异常信息
     */
    @Around(value = "pointCut()", argNames = "proceedingJoinPoint")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object proceed = proceedingJoinPoint.proceed();
        if (ObjectUtils.isNotEmpty(proceed) && proceed instanceof ApiResult) {
            HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
            HttpServletResponse response = RequestContextHolderUtils.getResponse();
            ResponseResult responseResult =
                    (ResponseResult) servletRequest.getAttribute(AppContextConstants.RESPONSE_RESULT_ANN);
            if (responseResult != null) {
                servletRequest.removeAttribute(AppContextConstants.RESPONSE_RESULT_ANN);
            }
            ApiResult<?> apiResult = (ApiResult<?>) proceed;
            ResponseResultStatus status = AnnotatedElementUtils.findMergedAnnotation(proceedingJoinPoint.getTarget().getClass(), ResponseResultStatus.class);
            int code = HttpStatus.BAD_REQUEST.value();
            if (ObjectUtils.isNotEmpty(status)) {
                code = status.value();
            }
            if (enableTransferStatus) {
                code = apiResult.getCode();
                response.setStatus(code);
            }
            // 判断是否是feign请求&& 是否需要异常传递
            Boolean feign = Convert.toBool(servletRequest.getHeader(AppContextConstants.REMOTE_CALL), Boolean.FALSE);
            if (feign && transferExceptionStatus) {
                response.setStatus(code);
            }

        }
        return proceed;
    }
}
