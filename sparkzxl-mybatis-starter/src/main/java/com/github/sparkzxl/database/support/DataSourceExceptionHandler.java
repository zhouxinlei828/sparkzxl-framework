package com.github.sparkzxl.database.support;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.TenantException;
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
@ResponseResultStatus
public class DataSourceExceptionHandler implements Ordered {

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ResponseResult<?> handleSqlSyntaxErrorException(SQLSyntaxErrorException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.SQL_EX);
    }

    @ExceptionHandler(TooManyResultsException.class)
    public ResponseResult<?> handleTooManyResultsException(TooManyResultsException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.SQL_MANY_RESULT_EX);
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseResult<?> handleBadSqlGrammarException(BadSqlGrammarException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.FAILURE.getCode(), e.getMessage());
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseResult<?> persistenceException(PersistenceException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        if (e.getCause() instanceof BizException) {
            BizException cause = (BizException) e.getCause();
            return ResponseResult.result(cause.getCode(), cause.getMessage());
        }
        return ResponseResult.result(ResponseInfoStatus.SQL_EX.getCode(), e.getMessage());
    }

    @ExceptionHandler(MyBatisSystemException.class)
    public ResponseResult<?> myBatisSystemException(MyBatisSystemException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        if (e.getCause() instanceof PersistenceException) {
            return this.persistenceException((PersistenceException) e.getCause());
        }
        return ResponseResult.result(ResponseInfoStatus.SQL_EX.getCode(), ResponseInfoStatus.SQL_EX.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public ResponseResult<?> sqlException(SQLException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.SQL_EX.getCode(), e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseResult<?> dataIntegrityViolationException(DataIntegrityViolationException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.SQL_EX.getCode(), e.getMessage());
    }

    @ExceptionHandler(TenantException.class)
    public ResponseResult<?> handleTenantException(TenantException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(e.getCode(), e.getMessage());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.DATASOURCE_EXCEPTION_HANDLER_ORDER.getOrder();
    }
}
