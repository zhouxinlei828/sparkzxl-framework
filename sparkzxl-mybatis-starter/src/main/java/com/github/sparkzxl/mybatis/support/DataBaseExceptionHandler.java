package com.github.sparkzxl.mybatis.support;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ReUtil;
import com.github.sparkzxl.core.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.TenantException;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.core.Ordered;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;

/**
 * description: 数据库全局异常处理
 *
 * @author zhoux
 */
@RestControllerAdvice
@RestController
@Slf4j
public class DataBaseExceptionHandler implements Ordered {

    private final static String DATABASE_PREFIX = "Unknown database";
    private final static String TABLE_PREFIX = "^Table.*doesn't exist$";
    private final static String COLUMN_PREFIX = "Unknown column";
    private final static int DATABASE_ERROR_CODE = 1364;

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public ApiResult<?> handleSqlSyntaxErrorException(SQLSyntaxErrorException e) {
        log.error("SQL异常：", e);
        return ApiResult.fail(ResultErrorCode.SQL_EX.getErrorCode(), ResultErrorCode.SQL_EX.getErrorMsg());
    }

    @ExceptionHandler(TooManyResultsException.class)
    public ApiResult<?> handleTooManyResultsException(TooManyResultsException e) {
        log.error("SQL异常：", e);
        return ApiResult.fail(
                ResultErrorCode.SQL_MANY_RESULT_EX.getErrorCode(), ResultErrorCode.SQL_MANY_RESULT_EX.getErrorMsg());
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ApiResult<?> handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error("SQL异常：", e);
        String message = e.getSQLException().getMessage();
        if (message.startsWith(DATABASE_PREFIX)) {
            return ApiResult.fail(
                    ResultErrorCode.UNKNOWN_DATABASE.getErrorCode(), ResultErrorCode.UNKNOWN_DATABASE.getErrorMsg());
        }
        if (ReUtil.isMatch(TABLE_PREFIX, message)) {
            return ApiResult.fail(
                    ResultErrorCode.UNKNOWN_TABLE.getErrorCode(), ResultErrorCode.UNKNOWN_TABLE.getErrorMsg());
        }
        if (message.startsWith(COLUMN_PREFIX)) {
            return ApiResult.fail(
                    ResultErrorCode.UNKNOWN_COLUMN.getErrorCode(), ResultErrorCode.UNKNOWN_COLUMN.getErrorMsg());
        }
        return ApiResult.fail(ResultErrorCode.FAILURE.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ApiResult<?> handleSqlIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        log.error("SQL完整性约束违反异常：", e);
        String message = e.getMessage();
        if (message.startsWith("Duplicate entry") && message.endsWith("for key 'PRIMARY'")) {
            return ApiResult.fail(ResultErrorCode.PRIMARY_KEY_CONFLICT_EXCEPTION.getErrorCode(), ResultErrorCode.PRIMARY_KEY_CONFLICT_EXCEPTION.getErrorMsg());
        }
        return ApiResult.fail(ResultErrorCode.SQL_EX.getErrorCode(), ResultErrorCode.SQL_EX.getErrorMsg());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResult<?> handlerDuplicateKeyException(DuplicateKeyException e) {
        log.error("数据重复输入: ", e);
        return ApiResult.fail(ResultErrorCode.SQL_EX.getErrorCode(), "数据重复冲突异常");
    }

    @ExceptionHandler(PersistenceException.class)
    public ApiResult<?> handlePersistenceException(PersistenceException e) {
        Throwable rootCause = ExceptionUtil.getRootCause(e);
        if (rootCause instanceof SQLIntegrityConstraintViolationException) {
            return handleSqlIntegrityConstraintViolationException((SQLIntegrityConstraintViolationException) rootCause);
        }
        log.error("数据库异常：", e);
        if (rootCause instanceof BizException) {
            BizException cause = (BizException) e.getCause();
            return ApiResult.fail(cause.getErrorCode(), cause.getMessage());
        }
        String message = e.getMessage();
        if (message.startsWith("Field '") && message.endsWith("' doesn't have a default value")) {
            return ApiResult.fail(ResultErrorCode.SQL_EX.getErrorCode(), "字段没有默认值");
        }
        return ApiResult.fail(ResultErrorCode.SQL_EX.getErrorCode(), ResultErrorCode.SQL_EX.getErrorMsg());
    }

    @ExceptionHandler(SQLException.class)
    public ApiResult<?> handleSqlException(SQLException e) {
        log.error("SQL异常：", e);
        return ApiResult.fail(ResultErrorCode.SQL_EX.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(TenantException.class)
    public ApiResult<?> handleTenantException(TenantException e) {
        log.error("租户异常：", e);
        return ApiResult.fail(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResult<?> handlerDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("数据库操作异常:", e);
        String message = e.getMessage();
        String prefix = "Data too long";
        if (message.contains(prefix)) {
            return ApiResult.fail(ResultErrorCode.COLUMN_DATA_TO_LONG_EXCEPTION.getErrorCode(), ResultErrorCode.COLUMN_DATA_TO_LONG_EXCEPTION.getErrorMsg());
        }
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            SQLException sqlException = (SQLException) cause;
            int errorCode = sqlException.getErrorCode();
            if (errorCode == DATABASE_ERROR_CODE) {
                return ApiResult.fail(ResultErrorCode.SQL_EX.getErrorCode(), "数据操作异常,输入参数为空");
            }
        }
        return ApiResult.fail(ResultErrorCode.SQL_EX.getErrorCode(), ResultErrorCode.SQL_EX.getErrorMsg());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.DATASOURCE_EXCEPTION_HANDLER_ORDER.getOrder();
    }
}
