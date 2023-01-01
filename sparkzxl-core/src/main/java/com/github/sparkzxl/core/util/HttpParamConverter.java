package com.github.sparkzxl.core.util;

import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.core.support.BaseUncheckedException;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description: The type Http param converter.
 *
 * @author zhouxinlei
 * @since 2022-08-12 17:16:05
 */
public final class HttpParamConverter {

    private static final Pattern PATTERN = Pattern.compile("([^&=]+)(=?)([^&]+)?");

    /**
     * of.
     *
     * @param supplier supplier
     * @return String string
     */
    public static String ofString(final Supplier<String> supplier) {
        return JsonUtils.getJson().toJson(initQueryParams(supplier.get()));
    }

    /**
     * map.
     *
     * @param <K>      the type parameter
     * @param <V>      the type parameter
     * @param supplier supplier
     * @return String string
     */
    public static <K, V> String toMap(final Supplier<MultiValueMap<K, V>> supplier) {
        return JsonUtils.getJson().toJson(supplier.get().toSingleValueMap());
    }

    /**
     * Init query params map.
     *
     * @param query the query
     * @return the map
     */
    public static Map<String, String> initQueryParams(final String query) {
        final Map<String, String> queryParams = new LinkedHashMap<>();
        if (StringUtils.hasLength(query)) {
            final Matcher matcher = PATTERN.matcher(query);
            while (matcher.find()) {
                String name = decodeQueryParam(matcher.group(1));
                String eq = matcher.group(2);
                String value = matcher.group(3);
                value = StringUtils.hasLength(value) ? decodeQueryParam(value) : (StringUtils.hasLength(eq) ? "" : null);
                queryParams.put(name, value);
            }
        }
        return queryParams;
    }

    /**
     * Decode query param string.
     *
     * @param value the value
     * @return the string
     */
    public static String decodeQueryParam(final String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new BaseUncheckedException(e);
        }
    }
}
