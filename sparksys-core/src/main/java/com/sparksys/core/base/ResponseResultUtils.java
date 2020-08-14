package com.sparksys.core.base;


import cn.hutool.json.JSONUtil;
import com.sparksys.core.base.result.ApiResult;
import com.sparksys.core.constant.BaseContextConstant;
import com.sparksys.core.support.ResponseResultStatus;
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
 * @date 2020-05-24 13:40:03
 */
@Slf4j
public class ResponseResultUtils {

    public static String getAuthHeader(HttpServletRequest httpRequest) {
        String header = httpRequest.getHeader(BaseContextConstant.JWT_TOKEN_HEADER);
        return StringUtils.removeStartIgnoreCase(header, BaseContextConstant.BEARER_TOKEN);
    }

    public static void unauthorized(HttpServletResponse response) {
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(ResponseResultStatus.UN_AUTHORIZED.getCode());
            response.getWriter().println(JSONUtil.parseObj(ApiResult.apiResult(ResponseResultStatus.UN_AUTHORIZED)).toStringPretty());
            log.info("");
            response.getWriter().flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public static void forbidden(HttpServletResponse response) {
        try {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(ResponseResultStatus.REQ_REJECT.getCode());
            response.getWriter().println(JSONUtil.parseObj(ApiResult.apiResult(ResponseResultStatus.REQ_REJECT)).toStringPretty());
            response.getWriter().flush();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.out.println(StandardCharsets.UTF_8.name());
    }
}
