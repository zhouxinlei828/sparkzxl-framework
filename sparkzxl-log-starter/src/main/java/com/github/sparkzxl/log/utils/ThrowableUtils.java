package com.github.sparkzxl.log.utils;


import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.entity.log.AlarmLogInfo;
import com.github.sparkzxl.log.AlarmLogContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * description: 异常处理工具类
 *
 * @author zhouxinlei
 */
public class ThrowableUtils {

    private static final String SEPARATOR = "\n";
    private static final String HTML_SEPARATOR = "<br />";

    public static String workWeChatContent(AlarmLogInfo context, Throwable throwable) {
        return defaultContent(context, throwable, SEPARATOR);
    }

    public static String dingTalkContent(AlarmLogInfo context, Throwable throwable) {
        return defaultContent(context, throwable, SEPARATOR);
    }

    public static Map<String, String> mailSubjectContent(AlarmLogInfo context, Throwable throwable) {
        Map<String, String> result = new HashMap<>(2);
        result.put("subject", context.getMessage());
        result.put("content", defaultContent(context, throwable, HTML_SEPARATOR));
        return result;
    }

    private static String defaultContent(AlarmLogInfo context, Throwable throwable, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!AlarmLogContext.getSimpleWarnInfo()) {
            stringBuilder.append("告警环境:").append(context.getEnvironment()).append(separator);
            stringBuilder.append("告警服务:").append(context.getApplicationName()).append(separator);
            stringBuilder.append("异常类:").append(context.getThrowableName()).append(separator);
            stringBuilder.append("错误信息:").append(context.getMessage()).append(separator);
            stringBuilder.append("线程信息:").append(context.getThreadName()).append(separator);
            stringBuilder.append("链路id:").append(context.getTraceId()).append(separator);
            stringBuilder.append("位置信息:").append(context.getClassName()).append(".").append(context.getMethodName()).append(isNativeMethod(context.getLineNumber()) ? "(Native Method)" : context.getFileName() != null && context.getLineNumber() >= 0 ? "(" + context.getFileName() + ":" + context.getLineNumber() + ")" : context.getFileName() != null ? "(" + context.getFileName() + ")" : "(Unknown Source)");
            stringBuilder.append(separator);
        } else {
            stringBuilder.append(context.getMessage()).append(separator);
        }
        if (AlarmLogContext.getPrintStackTrace()) {
            stringBuilder.append(printTrace(throwable));
        }
        return stringBuilder.toString();
    }

    public static String dingTalkMarkdownContent(AlarmLogInfo context, Throwable throwable) {
        return defaultMarkDownContent(context, throwable);
    }

    private static String defaultMarkDownContent(AlarmLogInfo context, Throwable throwable) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!AlarmLogContext.getSimpleWarnInfo()) {
            stringBuilder.append("> - 应用：<font color=\"#1890ff\">").append(context.getApplicationName()).append("</font>").append(ThrowableUtils.SEPARATOR);
            stringBuilder.append("> - 环境：<font color=\"#81C784\">").append(context.getEnvironment()).append("</font>\n------").append(ThrowableUtils.SEPARATOR);
            stringBuilder.append("- 异常类：").append(context.getThrowableName()).append(ThrowableUtils.SEPARATOR);
            stringBuilder.append("- 错误信息：<font color=\"#ff4837\">").append(context.getMessage()).append("</font>").append(ThrowableUtils.SEPARATOR);
            stringBuilder.append("- 线程信息：").append(context.getThreadName()).append(ThrowableUtils.SEPARATOR);
            stringBuilder.append("- 链路id：").append(context.getTraceId()).append(ThrowableUtils.SEPARATOR);
            stringBuilder.append("- 位置信息：")
                    .append(isNativeMethod(context.getLineNumber()) ?
                            "(Native Method)" :
                            context.getFileName() != null && context.getLineNumber() >= 0 ?
                                    "(" + context.getFileName() + ":" + context.getLineNumber() + ")" :
                                    context.getFileName() != null ? "(" + context.getFileName() + ")" : "(Unknown Source)");
            stringBuilder.append(ThrowableUtils.SEPARATOR);
        } else {
            stringBuilder.append(context.getMessage()).append(ThrowableUtils.SEPARATOR);
        }
        if (AlarmLogContext.getPrintStackTrace()) {
            stringBuilder.append("- 堆栈信息：").append(printTrace(throwable));
        }
        return stringBuilder.toString();
    }

    private static String printTrace(Throwable throwable) {
        if (throwable == null) {
            return "";
        }
        return ExceptionUtil.stacktraceToString(throwable,1000);
    }

    private static boolean isNativeMethod(int lineNumber) {
        return lineNumber == -2;
    }


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
