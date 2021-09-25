package com.github.sparkzxl.log;

import com.github.sparkzxl.log.utils.ThrowableUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 日志告警全局信息
 *
 * @author zhoux
 * @date 2021-08-22 09:35:44
 */
public class AlarmLogContext {

    private static final List<Class<? extends Throwable>> DO_EXTEND_WARN_EXCEPTION_LIST = new ArrayList<>();
    private static Boolean printStackTrace = false;
    private static Boolean simpleWarnInfo = false;
    private static Boolean warnExceptionExtend = false;
    private static List<Class<? extends Throwable>> doWarnExceptionList = new ArrayList<>();

    public static Boolean getPrintStackTrace() {
        return printStackTrace;
    }

    public static void setPrintStackTrace(Boolean printStackTrace) {
        AlarmLogContext.printStackTrace = printStackTrace;
    }

    public static Boolean getSimpleWarnInfo() {
        return simpleWarnInfo;
    }

    public static void setSimpleWarnInfo(Boolean simpleWarnInfo) {
        AlarmLogContext.simpleWarnInfo = simpleWarnInfo;
    }

    public static Boolean getWarnExceptionExtend() {
        return warnExceptionExtend;
    }

    public static void setWarnExceptionExtend(Boolean warnExceptionExtend) {
        AlarmLogContext.warnExceptionExtend = warnExceptionExtend;
        if (warnExceptionExtend && !AlarmLogContext.doWarnExceptionList.isEmpty()) {
            genExtendWarnExceptionList();
        }
    }

    public static List<Class<? extends Throwable>> getDoWarnExceptionList() {
        return doWarnExceptionList;
    }

    public static void setDoWarnExceptionList(List<Class<? extends Throwable>> doWarnExceptionList) {
        AlarmLogContext.doWarnExceptionList = doWarnExceptionList;
        if (AlarmLogContext.warnExceptionExtend) {
            genExtendWarnExceptionList();
        }
    }

    public static void addDoWarnExceptionList(List<Class<? extends Throwable>> doWarnExceptionList) {
        AlarmLogContext.doWarnExceptionList.addAll(doWarnExceptionList);
        if (AlarmLogContext.warnExceptionExtend) {
            genExtendWarnExceptionList(doWarnExceptionList);
        }
    }

    public static boolean doWarnException(Throwable warnExceptionClass) {
        return AlarmLogContext.warnExceptionExtend ? ThrowableUtils.doWarnExceptionExtend(warnExceptionClass, AlarmLogContext.DO_EXTEND_WARN_EXCEPTION_LIST) : ThrowableUtils.doWarnExceptionName(warnExceptionClass, AlarmLogContext.doWarnExceptionList);
    }

    private static void genExtendWarnExceptionList() {
        AlarmLogContext.DO_EXTEND_WARN_EXCEPTION_LIST.addAll(AlarmLogContext.doWarnExceptionList);
    }

    private static void genExtendWarnExceptionList(List<Class<? extends Throwable>> doWarnExceptionList) {
        AlarmLogContext.DO_EXTEND_WARN_EXCEPTION_LIST.addAll(doWarnExceptionList);
    }

}
