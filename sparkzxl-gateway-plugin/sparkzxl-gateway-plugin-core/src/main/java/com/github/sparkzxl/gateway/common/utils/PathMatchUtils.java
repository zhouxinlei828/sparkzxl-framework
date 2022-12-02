package com.github.sparkzxl.gateway.common.utils;

import org.springframework.http.server.PathContainer;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
