package com.github.sparkzxl.feign.exception;

import cn.hutool.core.date.DatePattern;
import com.github.sparkzxl.core.utils.DateUtils;
import com.github.sparkzxl.feign.config.FeignExceptionHandlerContext;
import com.github.sparkzxl.feign.model.ExceptionChain;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * description: feign远程调用异常
 *
 * @author zhouxinlei
 */
@Slf4j
public class RemoteCallException extends RuntimeException {

    private final List<StackTraceElement> stackTraceElements = new ArrayList<>(2);

    private boolean isAddThis = false;

    @Override
    public StackTraceElement[] getStackTrace() {
        if (stackTraceElements.isEmpty()) {
            return super.getStackTrace();
        }
        return stackTraceElements.toArray(new StackTraceElement[0]);
    }

    @Getter
    private List<ExceptionChain> exceptionChains;


    public RemoteCallException(String message) {
        super(message);
    }

    public RemoteCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteCallException(String message, List<ExceptionChain> exceptionChains) {
        super(message);
        this.exceptionChains = exceptionChains;
        if (CollectionUtils.isNotEmpty(exceptionChains)) {
            for (int i = 0; i < exceptionChains.size(); i++) {
                String status = i == 0 ? "HAPPEN" : "THROW";
                this.create(exceptionChains.get(i), status);
            }
        }

    }

    /**
     * 获取原始异常信息
     */
    public String getRawMessage() {
        ExceptionChain rawExceptionInfo = this.getRawExceptionInfo();
        return rawExceptionInfo == null ? null : rawExceptionInfo.getMsg();
    }

    public ExceptionChain getRawExceptionInfo() {
        return CollectionUtils.isEmpty(exceptionChains) ? null : exceptionChains.get(0);
    }

    /**
     * 判断异常是否为原始异常的子类
     *
     * @param exception 异常
     * @return boolean
     */
    public boolean isAssignableFrom(Class<? extends Throwable> exception) {
        ExceptionChain rawExceptionInfo = this.getRawExceptionInfo();
        return rawExceptionInfo != null && rawExceptionInfo.isAssignableFrom(exception);
    }

    @Override
    public String toString() {
        if (!isAddThis) {
            this.addThis();
            isAddThis = true;
        }
        return super.toString();
    }

    @Override
    public void printStackTrace() {
        if (!isAddThis) {
            this.addThis();
            isAddThis = true;
        }
        PrintStream err = System.err;
        err.println("cn.minsin.feign.exception.RemoteCallException : " + this.getMessage());
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            err.println("\t" + stackTraceElement);
        }
    }

    private void create(ExceptionChain exceptionChain, String status) {
        String format = "[%s]:[%s] timestamp:'%s',exceptionClass:'%s',message:'%s',path: '%s'";
        String str = String.format(format,
                status,
                exceptionChain.getApplicationName(),
                DateUtils.format(exceptionChain.getTimestamp(), DatePattern.NORM_DATETIME_MS_FORMAT),
                exceptionChain.getExceptionClass(),
                exceptionChain.getMsg(),
                exceptionChain.getPath()
        );
        StackTraceElement stackTraceElement = new StackTraceElement(
                str, "", "", 0
        );
        this.stackTraceElements.add(stackTraceElement);
    }

    private void addThis() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String requestPath = "";
        if (requestAttributes instanceof ServletRequestAttributes) {
            requestPath = ((ServletRequestAttributes) requestAttributes).getRequest().getRequestURI();
        }
        ExceptionChain exceptionChain = new ExceptionChain();
        exceptionChain.setApplicationName(FeignExceptionHandlerContext.getApplicationName());
        exceptionChain.setPath(requestPath);
        exceptionChain.setTimestamp(new Date());
        exceptionChain.setExceptionClass(RemoteCallException.class.getTypeName());
        exceptionChain.setMsg(this.getMessage());
        this.create(exceptionChain, "END");
    }

}
