package com.github.sparkzxl.zookeeper.support;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

/**
 * description: 缓存异常处理
 *
 * @author zhouxinlei
 */
@ControllerAdvice
@RestController
@Slf4j
@ResponseResultStatus
public class ZookeeperExceptionHandler implements Ordered {

    @ExceptionHandler(KeeperException.class)
    public ResponseResult<?> handleKeeperException(KeeperException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.FAILURE.getCode(), e.getMessage());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.ZOOKEEPER_EXCEPTION_ORDER.getOrder();
    }
}
