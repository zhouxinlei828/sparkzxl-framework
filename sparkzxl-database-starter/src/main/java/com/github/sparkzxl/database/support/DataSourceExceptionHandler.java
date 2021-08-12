package com.github.sparkzxl.database.support;

import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.TenantException;
import com.github.sparkzxl.core.utils.ResponseResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
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
 * @date 2021-08-03 21:16:12
 */
@RestControllerAdvice
@RestController
@Slf4j
public class DataSourceExceptionHandler implements Ordered {

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ApiResult<?> handleSqlSyntaxErrorException(SQLSyntaxErrorException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("IllegalArgumentException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.FAILURE.getCode(), e.getMessage());
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ApiResult<?> handleBadSqlGrammarException(BadSqlGrammarException e) {
        ResponseResultUtils.clearResponseResult();
        log.error("BadSqlGrammarException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.FAILURE.getCode(), e.getMessage());
    }

    @ExceptionHandler(PersistenceException.class)
    public ApiResult<?> persistenceException(PersistenceException ex) {
        ResponseResultUtils.clearResponseResult();
        log.error("PersistenceException:", ex);
        if (ex.getCause() instanceof BizException) {
            BizException cause = (BizException) ex.getCause();
            return ApiResult.apiResult(cause.getCode(), cause.getMessage());
        }
        return ApiResult.apiResult(ApiResponseStatus.SQL_EX.getCode(), ex.getMessage());
    }

    @ExceptionHandler(MyBatisSystemException.class)
    public ApiResult<?> myBatisSystemException(MyBatisSystemException ex) {
        ResponseResultUtils.clearResponseResult();
        log.error("PersistenceException:", ex);
        if (ex.getCause() instanceof PersistenceException) {
            return this.persistenceException((PersistenceException) ex.getCause());
        }
        return ApiResult.apiResult(ApiResponseStatus.SQL_EX.getCode(), ApiResponseStatus.SQL_EX.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public ApiResult<?> sqlException(SQLException ex) {
        ResponseResultUtils.clearResponseResult();
        log.error("SQLException:", ex);
        return ApiResult.apiResult(ApiResponseStatus.SQL_EX.getCode(), ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResult<?> dataIntegrityViolationException(DataIntegrityViolationException ex) {
        ResponseResultUtils.clearResponseResult();
        log.error("DataIntegrityViolationException:", ex);
        return ApiResult.apiResult(ApiResponseStatus.SQL_EX.getCode(), ex.getMessage());
    }

    @ExceptionHandler(TenantException.class)
    public ApiResult<?> handleTenantException(TenantException ex) {
        ResponseResultUtils.clearResponseResult();
        log.error("TenantException:", ex);
        return ApiResult.apiResult(ex.getCode(), ex.getMessage());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 15;
    }
}
