package com.github.sparkzxl.cache.support;

import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
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
    public ResponseResult<?> handleClusterRedirectException(ClusterRedirectException e) {
        log.error("ClusterRedirectException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.CLUSTER_REDIRECT_EXCEPTION);
    }

    @ExceptionHandler(JedisException.class)
    public ResponseResult<?> handleJedisException(JedisException e) {
        log.error("JedisException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.FAILURE.getCode(), e.getMessage());
    }

    @ExceptionHandler(ClusterStateFailureException.class)
    public ResponseResult<?> handleClusterStateFailureException(ClusterStateFailureException e) {
        log.error("ClusterStateFailureException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.CLUSTER_STATE_FAILURE_EXCEPTION);
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public ResponseResult<?> handleRedisConnectionFailureException(RedisConnectionFailureException e) {
        log.error("RedisConnectionFailureException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.REDIS_CONNECTION_FAILURE_EXCEPTION);
    }

    @ExceptionHandler(RedisSystemException.class)
    public ResponseResult<?> handleRedisSystemException(RedisSystemException e) {
        log.error("RedisSystemException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.REDIS_SYSTEM_EXCEPTION);
    }

    @ExceptionHandler(TooManyClusterRedirectionsException.class)
    public ResponseResult<?> handleTooManyClusterRedirectionsException(TooManyClusterRedirectionsException e) {
        log.error("TooManyClusterRedirectionsException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.TOO_MANY_CLUSTER_REDIRECTIONS_EXCEPTION);
    }

    @ExceptionHandler(ClusterCommandExecutionFailureException.class)
    public ResponseResult<?> handleTooManyClusterRedirectionsException(ClusterCommandExecutionFailureException e) {
        log.error("ClusterCommandExecutionFailureException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.CLUSTER_COMMAND_EXECUTION_FAILURE_EXCEPTION);
    }

    @ExceptionHandler(RedisSubscribedConnectionException.class)
    public ResponseResult<?> handleTooManyClusterRedirectionsException(RedisSubscribedConnectionException e) {
        log.error("RedisSubscribedConnectionException异常:", e);
        return ResponseResult.result(ResponseInfoStatus.REDIS_SUBSCRIBED_CONNECTION_EXCEPTION);
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.CACHE_EXCEPTION_ORDER.getOrder();
    }
}
