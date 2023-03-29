package com.github.sparkzxl.core.util;

import cn.hutool.core.util.URLUtil;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.PathContainer;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * description: Path match utils.
 *
 * @author zhouxinlei
 * @since 2022-08-12 14:30:21
 */
public class PathMatchUtils {

    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    /**
     * replace url {id} to real param.
     *
     * @param path        the total path
     * @param regex       the regex content
     * @param replacement the replacement content
     * @return the string
     */
    public static String replaceAll(final String path, final String regex, final String replacement) {
        return path.replaceAll(Pattern.quote(regex), Matcher.quoteReplacement(replacement));
    }

    /**
     * Match boolean.
     *
     * @param matchUrls the path pattern
     * @param realPath  the real path
     * @return the boolean
     */
    public static boolean match(final String matchUrls, final String realPath) {
        return MATCHER.match(matchUrls, realPath);
    }

    public static boolean matchUrl(List<String> list, String currentUrl) {
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        String path = currentUrl;
        if (StringUtils.startsWithAny(currentUrl, StrPool.HTTP, StrPool.HTTPS)) {
            URL url = URLUtil.url(currentUrl);
            path = url.getPath();
        }
        String finalPath = path;
        return list.stream().anyMatch((url) ->
                finalPath.startsWith(url) || MATCHER.match(url, finalPath)
        );
    }

    /**
     * Path pattern boolean.
     *
     * @param pathPattern the path pattern
     * @param realPath    the real path
     * @return the boolean
     */
    public static boolean pathPattern(final String pathPattern, final String realPath) {
        PathPattern pattern = PathPatternParser.defaultInstance.parse(pathPattern);
        return pattern.matches(PathContainer.parsePath(realPath));
    }
}
