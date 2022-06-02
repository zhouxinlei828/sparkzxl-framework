package com.github.sparkzxl.feign.util;

import com.github.sparkzxl.feign.exception.ExceptionPredicateFactory;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-06-01 08:47:19
 */
public final class NameUtils {

    private NameUtils() {
        throw new AssertionError("Must not instantiate utility class.");
    }

    /**
     * Generated name prefix.
     */
    public static final String GENERATED_NAME_PREFIX = "_genkey_";

    public static String generateName(int i) {
        return GENERATED_NAME_PREFIX + i;
    }

    public static String normalizeRoutePredicateName(Class<? extends ExceptionPredicateFactory> clazz) {
        return removeGarbage(clazz.getSimpleName().replace(ExceptionPredicateFactory.class.getSimpleName(), ""));
    }

    private static String removeGarbage(String s) {
        int garbageIdx = s.indexOf("$Mockito");
        if (garbageIdx > 0) {
            return s.substring(0, garbageIdx);
        }

        return s;
    }

}
