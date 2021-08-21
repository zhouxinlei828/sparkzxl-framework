package com.github.sparkzxl.database.support;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.TenantException;
import com.github.sparkzxl.core.utils.ResponseResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.core.Ordered;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;

/**
 * description: 数据源全局异常处理
 *
 * @author zhoux
 */
@RestControllerAdvice
@RestController
@Slf4j
public class DataSourceExceptionHandler implements Ordered {

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ApiResult<?> handleSqlSyntaxErrorException(SQLSyntaxErrorException e) {
        ResponseResultUtil.clearResponseResult();
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.SQL_EX);
    }

    @ExceptionHandler(TooManyResultsException.class)
    public ApiResult<?> handleTooManyResultsException(TooManyResultsException e) {
        ResponseResultUtil.clearResponseResult();
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.SQL_MANY_RESULT_EX);
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ApiResult<?> handleBadSqlGrammarException(BadSqlGrammarException e) {
        ResponseResultUtil.clearResponseResult();
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.FAILURE.getCode(), e.getMessage());
    }

    @ExceptionHandler(PersistenceException.class)
    public ApiResult<?> persistenceException(PersistenceException e) {
        ResponseResultUtil.clearResponseResult();
        log.error(ExceptionUtil.getMessage(e));
        if (e.getCause() instanceof BizException) {
            BizException cause = (BizException) e.getCause();
            return ApiResult.apiResult(cause.getCode(), cause.getMessage());
        }
        return ApiResult.apiResult(ApiResponseStatus.SQL_EX.getCode(), e.getMessage());
    }

    @ExceptionHandler(MyBatisSystemException.class)
    public ApiResult<?> myBatisSystemException(MyBatisSystemException e) {
        ResponseResultUtil.clearResponseResult();
        log.error(ExceptionUtil.getMessage(e));
        if (e.getCause() instanceof PersistenceException) {
            return this.persistenceException((PersistenceException) e.getCause());
        }
        return ApiResult.apiResult(ApiResponseStatus.SQL_EX.getCode(), ApiResponseStatus.SQL_EX.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public ApiResult<?> sqlException(SQLException e) {
        ResponseResultUtil.clearResponseResult();
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.SQL_EX.getCode(), e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResult<?> dataIntegrityViolationException(DataIntegrityViolationException e) {
        ResponseResultUtil.clearResponseResult();
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.SQL_EX.getCode(), e.getMessage());
    }

    @ExceptionHandler(TenantException.class)
    public ApiResult<?> handleTenantException(TenantException e) {
        ResponseResultUtil.clearResponseResult();
        log.error(ExceptionUtil.getMessage(e));
        return ApiResult.apiResult(e.getCode(), e.getMessage());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 15;
    }
}
