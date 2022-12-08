package com.github.sparkzxl.core.util;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.support.code.IErrorCode;
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
        String charEncoding = requestWrapper.getCharacterEncoding();
        try {
            return new String(requestWrapper.getContentAsByteArray(), charEncoding);
        } catch (UnsupportedEncodingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String parameterMapToString(Map<String, String[]> parameterMap) {
        return parameterMap.entrySet().stream()
                .map(e -> e.getKey() + StrPool.EQUALS +
                        Arrays.stream(e.getValue()).collect(Collectors.joining(StrPool.COMMA, StringUtils.EMPTY, StringUtils.EMPTY)))
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

    public static void failResponse(HttpServletResponse response, IErrorCode errorCode) {
        writeResponseOutMsg(response,
                false,
                null,
                null,
                errorCode.getErrorCode(),
                errorCode.getErrorMsg());
    }

    public static void failResponse(HttpServletResponse response, String errorCode, String errorMsg) {
        writeResponseOutMsg(response,
                false,
                null,
                null,
                errorCode,
                errorMsg);
    }

    public static void successResponse(HttpServletResponse response, String message) {
        writeResponseOutMsg(response, true, message, null, null, null);
    }


    public static <T> void successResponse(HttpServletResponse response, String message, T data) {
        writeResponseOutMsg(response, true, message, data, null, null);
    }

    public static <T> void writeResponseOutMsg(HttpServletResponse response, boolean success, String message, T data, String errorCode, String errorMsg) {
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            ApiResult<?> result;
            if (success) {
                result = ApiResult.success(message, data);
            } else {
                result = ApiResult.fail(errorCode, errorMsg);
            }
            response.getWriter().println(JsonUtil.toJson(result));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
