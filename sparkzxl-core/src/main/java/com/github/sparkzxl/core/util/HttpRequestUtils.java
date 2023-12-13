package com.github.sparkzxl.core.util;

import com.github.sparkzxl.core.base.result.R;
import com.github.sparkzxl.core.constant.BaseContextConstants;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.core.support.code.IErrorCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
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
     * 移除多余的/
     *
     * @param path 路径
     * @return String
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
     * URL解码
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
        int status = response.getStatus();
        writeResponseOutMsg(response,
                status,
                null,
                null,
                errorCode.getErrorCode(),
                errorCode.getErrorMsg());
    }

    public static void failResponse(HttpServletResponse response, String errorCode, String errorMsg) {
        int status = response.getStatus();
        writeResponseOutMsg(response,
                status,
                null,
                null,
                errorCode,
                errorMsg);
    }

    public static void successResponse(HttpServletResponse response, String message) {
        int status = response.getStatus();
        writeResponseOutMsg(response, status, message, null, null, null);
    }


    public static <T> void successResponse(HttpServletResponse response, String message, T data) {
        int status = response.getStatus();
        writeResponseOutMsg(response, status, message, data, null, null);
    }

    public static <T> void writeResponseOutMsg(HttpServletResponse response, int status, String message, T data, String errorCode,
                                               String errorMsg) {
        try {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Cache-Control", "no-cache");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            R<?> result;
            if (status == HttpStatus.OK.value()) {
                result = R.success(data);
            } else {
                result = R.failDetail(errorCode, errorMsg);
            }
            response.getWriter().println(JsonUtils.getJson().toJson(result));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("IO异常：", e);
        }
    }
}
