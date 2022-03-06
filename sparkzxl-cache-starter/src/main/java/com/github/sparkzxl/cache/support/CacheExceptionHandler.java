package com.github.sparkzxl.cache.support;

import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.entity.response.Response;
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
    public Response<?> handleClusterRedirectException(ClusterRedirectException e) {
        log.error("ClusterRedirectException异常:", e);
        return Response.failDetail(ExceptionErrorCode.CLUSTER_REDIRECT_EXCEPTION.getErrorCode(),
                ExceptionErrorCode.CLUSTER_REDIRECT_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(JedisException.class)
    public Response<?> handleJedisException(JedisException e) {
        log.error("JedisException异常:", e);
        return Response.failDetail(ExceptionErrorCode.FAILURE.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(ClusterStateFailureException.class)
    public Response<?> handleClusterStateFailureException(ClusterStateFailureException e) {
        log.error("ClusterStateFailureException异常:", e);
        return Response.failDetail(
                ExceptionErrorCode.CLUSTER_STATE_FAILURE_EXCEPTION.getErrorCode(), ExceptionErrorCode.CLUSTER_STATE_FAILURE_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public Response<?> handleRedisConnectionFailureException(RedisConnectionFailureException e) {
        log.error("RedisConnectionFailureException异常:", e);
        return Response.failDetail(
                ExceptionErrorCode.REDIS_CONNECTION_FAILURE_EXCEPTION.getErrorCode(), ExceptionErrorCode.REDIS_CONNECTION_FAILURE_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(RedisSystemException.class)
    public Response<?> handleRedisSystemException(RedisSystemException e) {
        log.error("RedisSystemException异常:", e);
        return Response.failDetail(
                ExceptionErrorCode.REDIS_SYSTEM_EXCEPTION.getErrorCode(), ExceptionErrorCode.REDIS_SYSTEM_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(TooManyClusterRedirectionsException.class)
    public Response<?> handleTooManyClusterRedirectionsException(TooManyClusterRedirectionsException e) {
        log.error("TooManyClusterRedirectionsException异常:", e);
        return Response.failDetail(
                ExceptionErrorCode.TOO_MANY_CLUSTER_REDIRECTIONS_EXCEPTION.getErrorCode(),
                ExceptionErrorCode.TOO_MANY_CLUSTER_REDIRECTIONS_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(ClusterCommandExecutionFailureException.class)
    public Response<?> handleTooManyClusterRedirectionsException(ClusterCommandExecutionFailureException e) {
        log.error("ClusterCommandExecutionFailureException异常:", e);
        return Response.failDetail(
                ExceptionErrorCode.CLUSTER_COMMAND_EXECUTION_FAILURE_EXCEPTION.getErrorCode(),
                ExceptionErrorCode.CLUSTER_COMMAND_EXECUTION_FAILURE_EXCEPTION.getErrorMessage());
    }

    @ExceptionHandler(RedisSubscribedConnectionException.class)
    public Response<?> handleTooManyClusterRedirectionsException(RedisSubscribedConnectionException e) {
        log.error("RedisSubscribedConnectionException异常:", e);
        return Response.failDetail(
                ExceptionErrorCode.REDIS_SUBSCRIBED_CONNECTION_EXCEPTION.getErrorCode(),
                ExceptionErrorCode.REDIS_SUBSCRIBED_CONNECTION_EXCEPTION.getErrorMessage());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.CACHE_EXCEPTION_ORDER.getOrder();
    }
}
