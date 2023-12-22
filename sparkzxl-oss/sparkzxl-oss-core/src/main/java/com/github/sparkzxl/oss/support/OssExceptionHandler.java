package com.github.sparkzxl.oss.support;

import com.github.sparkzxl.core.base.result.R;
import com.github.sparkzxl.core.constant.enums.BeanOrderEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * description: 告警全局异常处理
 *
 * @author zhouxinlei
 */
@Slf4j
@RestControllerAdvice
public class OssExceptionHandler implements Ordered {

    @ExceptionHandler(OssException.class)
    public R<?> handleOssException(OssException e) {
        log.error("OssException异常:", e);
        return R.failDetail(e.getErrorCode(), e.getErrorMsg());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.OSS_EXCEPTION_ORDER.getOrder();
    }
}
