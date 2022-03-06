package com.github.sparkzxl.database.support;

import cn.hutool.core.util.ReUtil;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.TenantException;
import com.github.sparkzxl.entity.response.Response;
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
import java.sql.SQLSyntaxErrorException;

/**
 * description: 数据库全局异常处理
 *
 * @author zhoux
 */
@RestControllerAdvice
@RestController
@Slf4j
@ResponseResultStatus
public class DataBaseExceptionHandler implements Ordered {

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Response<?> handleSqlSyntaxErrorException(SQLSyntaxErrorException e) {
        log.error("SQL异常：", e);
        return Response.failDetail(ExceptionErrorCode.SQL_EX.getErrorCode(), ExceptionErrorCode.SQL_EX.getErrorMessage());
    }

    @ExceptionHandler(TooManyResultsException.class)
    public Response<?> handleTooManyResultsException(TooManyResultsException e) {
        log.error("查询异常：", e);
        return Response.failDetail(
                ExceptionErrorCode.SQL_MANY_RESULT_EX.getErrorCode(), ExceptionErrorCode.SQL_MANY_RESULT_EX.getErrorMessage());
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public Response<?> handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error("SQL异常：", e);
        String message = e.getSQLException().getMessage();
        if (message.startsWith("Unknown database")) {
            return Response.failDetail(
                    ExceptionErrorCode.UNKNOWN_DATABASE.getErrorCode(), ExceptionErrorCode.UNKNOWN_DATABASE.getErrorMessage());
        }
        if (ReUtil.isMatch("^Table.*doesn't exist$", message)) {
            return Response.failDetail(
                    ExceptionErrorCode.UNKNOWN_TABLE.getErrorCode(), ExceptionErrorCode.UNKNOWN_TABLE.getErrorMessage());
        }
        if (message.startsWith("Unknown column")) {
            return Response.failDetail(
                    ExceptionErrorCode.UNKNOWN_COLUMN.getErrorCode(), ExceptionErrorCode.UNKNOWN_COLUMN.getErrorMessage());
        }
        return Response.failDetail(ExceptionErrorCode.FAILURE.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(PersistenceException.class)
    public Response<?> persistenceException(PersistenceException e) {
        log.error("数据库异常：", e);
        if (e.getCause() instanceof BizException) {
            BizException cause = (BizException) e.getCause();
            return Response.failDetail(cause.getErrorCode(), cause.getMessage());
        }
        return Response.failDetail(ExceptionErrorCode.SQL_EX.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public Response<?> sqlException(SQLException e) {
        log.error("SQL异常：", e);
        return Response.failDetail(ExceptionErrorCode.SQL_EX.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(TenantException.class)
    public Response<?> handleTenantException(TenantException e) {
        log.error("租户异常：", e);
        return Response.failDetail(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Response<?> handler(DuplicateKeyException e) {
        log.error("数据重复输入: ", e);
        return Response.failDetail(ExceptionErrorCode.SQL_EX.getErrorCode(), "数据重复冲突异常");
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public Response<?> handler(DataIntegrityViolationException e) {
        log.error("数据库操作异常:", e);
        String message = e.getMessage();
        String prefix = "Data too long";
        if (message.contains(prefix)) {
            return Response.failDetail(ExceptionErrorCode.SQL_EX.getErrorCode(), "输入数据字段过长");
        }
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            SQLException sqlException = (SQLException) cause;
            int errorCode = sqlException.getErrorCode();
            if (errorCode == 1364) {
                return Response.failDetail(ExceptionErrorCode.SQL_EX.getErrorCode(), "数据操作异常,输入参数为空");
            }
        }
        return Response.failDetail(
                ExceptionErrorCode.SQL_EX.getErrorCode(), ExceptionErrorCode.SQL_EX.getErrorMessage());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.DATASOURCE_EXCEPTION_HANDLER_ORDER.getOrder();
    }
}
