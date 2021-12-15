package com.github.sparkzxl.idempotent.support;

import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * description: 幂等全局异常处理
 *
 * @author zhouxinlei
 */
@RestControllerAdvice
@RestController
@Slf4j
@ResponseResultStatus
public class IdempotentExceptionHandler implements Ordered {

    @ExceptionHandler(IdempotentNoLockException.class)
    public ResponseResult<?> handleIdempotentNoLockException(IdempotentNoLockException e) {
        log.error("SQL异常：", e);
        return ResponseResult.result(ResponseInfoStatus.FAILURE.getCode(), e.getMessage());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.IDEMPOTENT_EXCEPTION_ORDER.getOrder();
    }
}
