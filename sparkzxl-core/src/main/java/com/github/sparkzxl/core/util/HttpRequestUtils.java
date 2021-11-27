package com.github.sparkzxl.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description: Http请求工具
 *
 * @author zhouxinlei
 * @date 2021-11-27 12:11:18
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpRequestUtils {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * 是否为json 请求
     *
     * @param contentType
     * @return
     */
    public static boolean isJsonRequest(String contentType) {
        return StringUtils.isNotBlank(contentType) &&
                MediaType.valueOf(contentType).isCompatibleWith(MediaType.APPLICATION_JSON);
    }

    /**
     * 获取请求属性
     *
     * @param requestAttributes
     * @param name
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getRequestAttribute(RequestAttributes requestAttributes, String name) {
        return (T) requestAttributes.getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

    /**
     * 读取json请求流
     *
     * @param request
     * @return
     * @throws IOException
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
        String str = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = null;
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
     * @param pattern
     * @param path
     * @return
     */
    public static boolean isMatchPath(String pattern, String path) {
        return ANT_PATH_MATCHER.match(pattern, path);
    }

    /**
     * 出去多余的 /
     *
     * @param path
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
     * @param str
     * @return
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

}
