package com.github.sparkzxl.cache.support;

import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ExceptionCode;
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
        return Response.fail(ExceptionCode.CLUSTER_REDIRECT_EXCEPTION.getCode(), ExceptionCode.CLUSTER_REDIRECT_EXCEPTION.getMessage());
    }

    @ExceptionHandler(JedisException.class)
    public Response<?> handleJedisException(JedisException e) {
        log.error("JedisException异常:", e);
        return Response.fail(ExceptionCode.FAILURE.getCode(), e.getMessage());
    }

    @ExceptionHandler(ClusterStateFailureException.class)
    public Response<?> handleClusterStateFailureException(ClusterStateFailureException e) {
        log.error("ClusterStateFailureException异常:", e);
        return Response.fail(ExceptionCode.CLUSTER_STATE_FAILURE_EXCEPTION.getCode(), ExceptionCode.CLUSTER_STATE_FAILURE_EXCEPTION.getMessage());
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public Response<?> handleRedisConnectionFailureException(RedisConnectionFailureException e) {
        log.error("RedisConnectionFailureException异常:", e);
        return Response.fail(ExceptionCode.REDIS_CONNECTION_FAILURE_EXCEPTION.getCode(), ExceptionCode.REDIS_CONNECTION_FAILURE_EXCEPTION.getMessage());
    }

    @ExceptionHandler(RedisSystemException.class)
    public Response<?> handleRedisSystemException(RedisSystemException e) {
        log.error("RedisSystemException异常:", e);
        return Response.fail(ExceptionCode.REDIS_SYSTEM_EXCEPTION.getCode(), ExceptionCode.REDIS_SYSTEM_EXCEPTION.getMessage());
    }

    @ExceptionHandler(TooManyClusterRedirectionsException.class)
    public Response<?> handleTooManyClusterRedirectionsException(TooManyClusterRedirectionsException e) {
        log.error("TooManyClusterRedirectionsException异常:", e);
        return Response.fail(ExceptionCode.TOO_MANY_CLUSTER_REDIRECTIONS_EXCEPTION.getCode(), ExceptionCode.TOO_MANY_CLUSTER_REDIRECTIONS_EXCEPTION.getMessage());
    }

    @ExceptionHandler(ClusterCommandExecutionFailureException.class)
    public Response<?> handleTooManyClusterRedirectionsException(ClusterCommandExecutionFailureException e) {
        log.error("ClusterCommandExecutionFailureException异常:", e);
        return Response.fail(ExceptionCode.CLUSTER_COMMAND_EXECUTION_FAILURE_EXCEPTION.getCode(), ExceptionCode.CLUSTER_COMMAND_EXECUTION_FAILURE_EXCEPTION.getMessage());
    }

    @ExceptionHandler(RedisSubscribedConnectionException.class)
    public Response<?> handleTooManyClusterRedirectionsException(RedisSubscribedConnectionException e) {
        log.error("RedisSubscribedConnectionException异常:", e);
        return Response.fail(ExceptionCode.REDIS_SUBSCRIBED_CONNECTION_EXCEPTION.getCode(), ExceptionCode.REDIS_SUBSCRIBED_CONNECTION_EXCEPTION.getMessage());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.CACHE_EXCEPTION_ORDER.getOrder();
    }
}
