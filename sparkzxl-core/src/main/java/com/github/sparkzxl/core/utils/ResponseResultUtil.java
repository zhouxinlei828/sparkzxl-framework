package com.github.sparkzxl.core.utils;


import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.annotation.result.ResponseResult;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * description: ResponseResult工具类
 *
 * @author zhouxinlei
 */
@Slf4j
public class ResponseResultUtil {

    public static String getAuthHeader(HttpServletRequest httpRequest) {
        String header = httpRequest.getHeader(BaseContextConstants.JWT_TOKEN_HEADER);
        return StringUtils.removeStartIgnoreCase(header, BaseContextConstants.BEARER_TOKEN);
    }

    public static void writeResponseOutMsg(HttpServletResponse response, int code, String msg) {
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().println(JSONUtil.parseObj(ApiResult.apiResult(code, msg)).toStringPretty());
            response.getWriter().flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void writeResponseOutMsg(HttpServletResponse response, int code, String msg, Object data) {
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().println(JSONUtil.parseObj(ApiResult.apiResult(code, msg, data)).toStringPretty());
            response.getWriter().flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void unauthorized(HttpServletResponse response, String msg) {
        try {
            int code = ApiResponseStatus.UN_AUTHORIZED.getCode();
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(code);
            response.getWriter().println(JSONUtil.parseObj(ApiResult.apiResult(code, msg)).toStringPretty());
            response.getWriter().flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void forbidden(HttpServletResponse response, String msg) {
        try {
            int code = ApiResponseStatus.AUTHORIZED_DENIED.getCode();
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(ApiResponseStatus.AUTHORIZED_DENIED.getCode());
            response.getWriter().println(JSONUtil.parseObj(ApiResult.apiResult(code, msg)).toStringPretty());
            response.getWriter().flush();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void clearResponseResult() {
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        ResponseResult responseResult =
                (ResponseResult) servletRequest.getAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        if (responseResult != null) {
            servletRequest.removeAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        }
    }
}
