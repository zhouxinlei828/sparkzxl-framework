package com.github.sparkzxl.cache.support;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.data.redis.*;
import org.springframework.data.redis.connection.ClusterCommandExecutionFailureException;
import org.springframework.data.redis.connection.RedisSubscribedConnectionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.exceptions.JedisException;

/**
 * description: 缓存异常处理
 *
 * @author zhouxinlei
 */
@ControllerAdvice
@ResponseResultStatus
@RestController
@Slf4j
public class CacheExceptionHandler implements Ordered {

    @ExceptionHandler(ClusterRedirectException.class)
    public ApiResult<?> handleClusterRedirectException(ClusterRedirectException e) {
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.CLUSTER_REDIRECT_EXCEPTION);
    }

    @ExceptionHandler(JedisException.class)
    public ApiResult<?> handleJedisException(JedisException e) {
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.FAILURE.getCode(), e.getMessage());
    }

    @ExceptionHandler(ClusterStateFailureException.class)
    public ApiResult<?> handleClusterStateFailureException(ClusterStateFailureException e) {
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.CLUSTER_STATE_FAILURE_EXCEPTION);
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ApiResult<?> handleRedisConnectionFailureException(RedisConnectionFailureException e) {
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.REDIS_CONNECTION_FAILURE_EXCEPTION);
    }

    @ExceptionHandler(RedisSystemException.class)
    public ApiResult<?> handleRedisSystemException(RedisSystemException e) {
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.REDIS_SYSTEM_EXCEPTION);
    }

    @ExceptionHandler(TooManyClusterRedirectionsException.class)
    public ApiResult<?> handleTooManyClusterRedirectionsException(TooManyClusterRedirectionsException e) {
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.TOO_MANY_CLUSTER_REDIRECTIONS_EXCEPTION);
    }

    @ExceptionHandler(ClusterCommandExecutionFailureException.class)
    public ApiResult<?> handleTooManyClusterRedirectionsException(ClusterCommandExecutionFailureException e) {
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.CLUSTER_COMMAND_EXECUTION_FAILURE_EXCEPTION);
    }

    @ExceptionHandler(RedisSubscribedConnectionException.class)
    public ApiResult<?> handleTooManyClusterRedirectionsException(RedisSubscribedConnectionException e) {
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.REDIS_SUBSCRIBED_CONNECTION_EXCEPTION);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 12;
    }
}
