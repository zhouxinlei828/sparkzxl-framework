package com.github.sparkzxl.cache.support;

import com.github.sparkzxl.core.base.result.R;
import com.github.sparkzxl.core.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.support.code.ExceptionErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.data.redis.ClusterRedirectException;
import org.springframework.data.redis.ClusterStateFailureException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.TooManyClusterRedirectionsException;
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
@RestController
@Slf4j
public class CacheExceptionHandler implements Ordered {

    @ExceptionHandler(ClusterRedirectException.class)
    public R<?> handleClusterRedirectException(ClusterRedirectException e) {
        log.error("ClusterRedirectException 异常:", e);
        return R.failDetail(ExceptionErrorCode.CLUSTER_REDIRECT_EXCEPTION.getErrorCode(),
                ExceptionErrorCode.CLUSTER_REDIRECT_EXCEPTION.getErrorMsg());
    }

    @ExceptionHandler(JedisException.class)
    public R<?> handleJedisException(JedisException e) {
        log.error("JedisException 异常:", e);
        return R.failDetail(ExceptionErrorCode.FAILURE.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(ClusterStateFailureException.class)
    public R<?> handleClusterStateFailureException(ClusterStateFailureException e) {
        log.error("ClusterStateFailureException 异常:", e);
        return R.failDetail(
                ExceptionErrorCode.CLUSTER_STATE_FAILURE_EXCEPTION.getErrorCode(),
                ExceptionErrorCode.CLUSTER_STATE_FAILURE_EXCEPTION.getErrorMsg());
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    public R<?> handleRedisConnectionFailureException(RedisConnectionFailureException e) {
        log.error("RedisConnectionFailureException 异常:", e);
        return R.failDetail(
                ExceptionErrorCode.REDIS_CONNECTION_FAILURE_EXCEPTION.getErrorCode(),
                ExceptionErrorCode.REDIS_CONNECTION_FAILURE_EXCEPTION.getErrorMsg());
    }

    @ExceptionHandler(RedisSystemException.class)
    public R<?> handleRedisSystemException(RedisSystemException e) {
        log.error("RedisSystemException 异常:", e);
        return R.failDetail(
                ExceptionErrorCode.REDIS_SYSTEM_EXCEPTION.getErrorCode(), ExceptionErrorCode.REDIS_SYSTEM_EXCEPTION.getErrorMsg());
    }

    @ExceptionHandler(TooManyClusterRedirectionsException.class)
    public R<?> handleTooManyClusterRedirectionsException(TooManyClusterRedirectionsException e) {
        log.error("TooManyClusterRedirectionsException 异常:", e);
        return R.failDetail(
                ExceptionErrorCode.TOO_MANY_CLUSTER_REDIRECTIONS_EXCEPTION.getErrorCode(),
                ExceptionErrorCode.TOO_MANY_CLUSTER_REDIRECTIONS_EXCEPTION.getErrorMsg());
    }

    @ExceptionHandler(ClusterCommandExecutionFailureException.class)
    public R<?> handleTooManyClusterRedirectionsException(ClusterCommandExecutionFailureException e) {
        log.error("ClusterCommandExecutionFailureException 异常:", e);
        return R.failDetail(
                ExceptionErrorCode.CLUSTER_COMMAND_EXECUTION_FAILURE_EXCEPTION.getErrorCode(),
                ExceptionErrorCode.CLUSTER_COMMAND_EXECUTION_FAILURE_EXCEPTION.getErrorMsg());
    }

    @ExceptionHandler(RedisSubscribedConnectionException.class)
    public R<?> handleTooManyClusterRedirectionsException(RedisSubscribedConnectionException e) {
        log.error("RedisSubscribedConnectionException 异常:", e);
        return R.failDetail(
                ExceptionErrorCode.REDIS_SUBSCRIBED_CONNECTION_EXCEPTION.getErrorCode(),
                ExceptionErrorCode.REDIS_SUBSCRIBED_CONNECTION_EXCEPTION.getErrorMsg());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.CACHE_EXCEPTION_ORDER.getOrder();
    }
}
