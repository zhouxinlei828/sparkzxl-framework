package com.github.sparkzxl.utils;



import java.util.List;

/**
 * @author weilai
 */
public class ExceptionUtils {

    public static boolean doWarnExceptionName(Throwable warnExceptionClass, List<Class<? extends Throwable>> doWarnExceptionList) {
        return doWarnExceptionList.contains(warnExceptionClass.getClass());
    }

    public static boolean doWarnExceptionExtend(Throwable warnExceptionClass, List<Class<? extends Throwable>> doExtendWarnExceptionList) {
        for (Class<?> aClass : doExtendWarnExceptionList) {
            if (aClass.isAssignableFrom(warnExceptionClass.getClass())) {
                return true;
            }
        }
        return false;
    }
}
