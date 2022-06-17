package com.github.sparkzxl.database.support;

import cn.hutool.core.util.ReUtil;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import com.github.sparkzxl.core.support.BizException;
import com.github.sparkzxl.core.support.TenantException;
import com.github.sparkzxl.core.base.result.Response;
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
public class DataBaseExceptionHandler implements Ordered {

    private final static String DATABASE_PREFIX = "Unknown database";
    private final static String TABLE_PREFIX = "^Table.*doesn't exist$";
    private final static String COLUMN_PREFIX = "Unknown column";
    private final static int DATABASE_ERROR_CODE = 1364;

    @ExceptionHandler(SQLSyntaxErrorException.class)
    public Response<?> handleSqlSyntaxErrorException(SQLSyntaxErrorException e) {
        log.error("SQL异常：", e);
        return Response.fail(ResultErrorCode.SQL_EX.getErrorCode(), ResultErrorCode.SQL_EX.getErrorMsg());
    }

    @ExceptionHandler(TooManyResultsException.class)
    public Response<?> handleTooManyResultsException(TooManyResultsException e) {
        log.error("查询异常：", e);
        return Response.fail(
                ResultErrorCode.SQL_MANY_RESULT_EX.getErrorCode(), ResultErrorCode.SQL_MANY_RESULT_EX.getErrorMsg());
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public Response<?> handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error("SQL异常：", e);
        String message = e.getSQLException().getMessage();
        if (message.startsWith(DATABASE_PREFIX)) {
            return Response.fail(
                    ResultErrorCode.UNKNOWN_DATABASE.getErrorCode(), ResultErrorCode.UNKNOWN_DATABASE.getErrorMsg());
        }
        if (ReUtil.isMatch(TABLE_PREFIX, message)) {
            return Response.fail(
                    ResultErrorCode.UNKNOWN_TABLE.getErrorCode(), ResultErrorCode.UNKNOWN_TABLE.getErrorMsg());
        }
        if (message.startsWith(COLUMN_PREFIX)) {
            return Response.fail(
                    ResultErrorCode.UNKNOWN_COLUMN.getErrorCode(), ResultErrorCode.UNKNOWN_COLUMN.getErrorMsg());
        }
        return Response.fail(ResultErrorCode.FAILURE.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(PersistenceException.class)
    public Response<?> persistenceException(PersistenceException e) {
        log.error("数据库异常：", e);
        if (e.getCause() instanceof BizException) {
            BizException cause = (BizException) e.getCause();
            return Response.fail(cause.getErrorCode(), cause.getMessage());
        }
        return Response.fail(ResultErrorCode.SQL_EX.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(SQLException.class)
    public Response<?> sqlException(SQLException e) {
        log.error("SQL异常：", e);
        return Response.fail(ResultErrorCode.SQL_EX.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(TenantException.class)
    public Response<?> handleTenantException(TenantException e) {
        log.error("租户异常：", e);
        return Response.fail(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Response<?> handler(DuplicateKeyException e) {
        log.error("数据重复输入: ", e);
        return Response.fail(ResultErrorCode.SQL_EX.getErrorCode(), "数据重复冲突异常");
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public Response<?> handler(DataIntegrityViolationException e) {
        log.error("数据库操作异常:", e);
        String message = e.getMessage();
        String prefix = "Data too long";
        if (message.contains(prefix)) {
            return Response.fail(ResultErrorCode.SQL_EX.getErrorCode(), "输入数据字段过长");
        }
        Throwable cause = e.getCause();
        if (cause instanceof SQLException) {
            SQLException sqlException = (SQLException) cause;
            int errorCode = sqlException.getErrorCode();
            if (errorCode == DATABASE_ERROR_CODE) {
                return Response.fail(ResultErrorCode.SQL_EX.getErrorCode(), "数据操作异常,输入参数为空");
            }
        }
        return Response.fail(
                ResultErrorCode.SQL_EX.getErrorCode(), ResultErrorCode.SQL_EX.getErrorMsg());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.DATASOURCE_EXCEPTION_HANDLER_ORDER.getOrder();
    }
}
