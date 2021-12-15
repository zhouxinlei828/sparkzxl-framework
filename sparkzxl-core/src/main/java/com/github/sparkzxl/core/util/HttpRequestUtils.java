package com.github.sparkzxl.core.util;

import com.github.sparkzxl.annotation.response.Response;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
import com.github.sparkzxl.core.jackson.JsonUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description: Http请求工具
 *
 * @author zhouxinlei
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpRequestUtils {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * 是否为json 请求
     *
     * @param contentType contentType
     * @return boolean
     */
    public static boolean isJsonRequest(String contentType) {
        return StringUtils.isNotBlank(contentType) &&
                MediaType.valueOf(contentType).isCompatibleWith(MediaType.APPLICATION_JSON);
    }

    /**
     * 获取请求属性
     *
     * @param requestAttributes 请求属性
     * @param name              key
     * @return T
     */
    @SuppressWarnings("unchecked")
    public static <T> T getRequestAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 读取json请求流
     *
     * @param request    请求
     * @param useWrapper useWrapper
     * @return String
     */
    public static String readRequestBodyForJson(HttpServletRequest request, boolean useWrapper) {
        if (request instanceof ContentCachingRequestWrapper && useWrapper) {
            return readFromRequestWrapper((ContentCachingRequestWrapper) request);
        } else if (!useWrapper) {
            return readFromRequest(request);
        }
        return StringUtils.EMPTY;
    }

    public static String readFromRequestWrapper(ContentCachingRequestWrapper requestWrapper) {
        String charEncoding = requestWrapper.getCharacterEncoding() != null ? requestWrapper.getCharacterEncoding() : StrPool.UTF8;
        try {
            return new String(requestWrapper.getContentAsByteArray(), charEncoding);
        } catch (UnsupportedEncodingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String parameterMapToString(Map<String, String[]> parameterMap) {
        return parameterMap.entrySet().stream()
                .map(e ->
                        e.getKey() + StrPool.EQUALS +
                                Arrays.stream(e.getValue())
                                        .collect(Collectors.joining(StrPool.COMMA, StringUtils.EMPTY, StringUtils.EMPTY)))
                .collect(Collectors.joining(StrPool.AMPERSAND, StringUtils.EMPTY, StringUtils.EMPTY));
    }


    public static String readFromRequest(HttpServletRequest request) {
        String str;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader;
        try {
            reader = request.getReader();
            while ((str = reader.readLine()) != null) {
                stringBuilder.append(str).append(LINE_SEPARATOR);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        if (stringBuilder.length() > 0) {
            return stringBuilder.substring(0, stringBuilder.length() - LINE_SEPARATOR.length());
        }
        return StringUtils.EMPTY;
    }

    public static boolean isIncludePayload(HttpServletRequest request) {
        int contentLength = request.getContentLength();
        return contentLength != 0;
    }

    /**
     * 路径是否匹配
     *
     * @param pattern pattern
     * @param path    路径
     * @return boolean
     */
    public static boolean isMatchPath(String pattern, String path) {
        return ANT_PATH_MATCHER.match(pattern, path);
    }

    /**
     * 移除多余的 /
     *
     * @param path 路径
     * @return
     */
    public static String sanitizedPath(final String path) {
        String sanitized = path;
        while (true) {
            int index = sanitized.indexOf("//");
            if (index < 0) {
                break;
            } else {
                sanitized = sanitized.substring(0, index) + sanitized.substring(index + 1);
            }
        }
        return sanitized;
    }


    /**
     * URL 解码
     *
     * @param str 字符串
     * @return String
     */
    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, StrPool.UTF8);
        } catch (UnsupportedEncodingException e) {
            throw new UncheckedIOException(e);
        }
    }


    public static String readRequestBodyForJson(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return readFromRequestWrapper((ContentCachingRequestWrapper) request);
        } else {
            return readFromRequest(request);
        }
    }

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
