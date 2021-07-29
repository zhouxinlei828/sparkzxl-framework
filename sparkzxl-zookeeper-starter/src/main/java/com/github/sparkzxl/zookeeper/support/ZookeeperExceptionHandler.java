package com.github.sparkzxl.zookeeper.support;

import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.utils.ResponseResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.KeeperException;
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
public class ZookeeperExceptionHandler {

    @ExceptionHandler(KeeperException.class)
    public ApiResult handleKeeperException(KeeperException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("ClusterRedirectException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.FAILURE.getCode(), e.getMessage());
    }

}
