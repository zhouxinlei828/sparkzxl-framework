package com.github.sparkzxl.core.context;


import com.github.sparkzxl.annotation.response.Response;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.utils.RequestContextHolderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * description: 响应处理工具类
 *
 * @author zhouxinlei
 */
@Slf4j
public class ResponseHelper {

    public static String getAuthHeader(HttpServletRequest httpRequest) {
        String header = httpRequest.getHeader(BaseContextConstants.JWT_TOKEN_HEADER);
        return StringUtils.removeStartIgnoreCase(header, BaseContextConstants.BEARER_TOKEN);
    }

    public static void writeResponseOutMsg(HttpServletResponse response, int code, String msg) {
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().println(JsonUtil.toJson(ResponseResult.result(code, msg)));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void writeResponseOutMsg(HttpServletResponse response, int code, String msg, Object data) {
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().println(JsonUtil.toJson(ResponseResult.result(code, msg, data)));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void unauthorized(HttpServletResponse response, String msg) {
        try {
            int code = ResponseInfoStatus.UN_AUTHORIZED.getCode();
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(code);
            response.getWriter().println(JsonUtil.toJson(ResponseResult.result(code, msg)));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void forbidden(HttpServletResponse response, String msg) {
        try {
            int code = ResponseInfoStatus.AUTHORIZED_DENIED.getCode();
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(ResponseInfoStatus.AUTHORIZED_DENIED.getCode());
            response.getWriter().println(JsonUtil.toJson(ResponseResult.result(code, msg)));
            response.getWriter().flush();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void clearResponseResult() {
        HttpServletRequest servletRequest = RequestContextHolderUtils.getRequest();
        Response response =
                (Response) servletRequest.getAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        if (response != null) {
            servletRequest.removeAttribute(BaseContextConstants.RESPONSE_RESULT_ANN);
        }
    }
}
