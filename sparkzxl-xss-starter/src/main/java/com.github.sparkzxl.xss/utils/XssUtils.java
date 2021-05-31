package com.github.sparkzxl.xss.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.core.utils.StrPool;
import lombok.extern.slf4j.Slf4j;
import org.owasp.validator.html.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * description: XSS 工具类， 用于过滤特殊字符
 *
 * @author zhouxinlei
 * @date 2021-05-31 14:02:59
 */
@Slf4j
public final class XssUtils {

    private XssUtils() {
    }

    private static final String ANTISAMY_SLASHDOT_XML = "antisamy-slashdot-1.4.4.xml";
    private static Policy policy = null;

    static {
        InputStream inputStream = XssUtils.class.getClassLoader().getResourceAsStream(ANTISAMY_SLASHDOT_XML);
        try {
            policy = Policy.getInstance(inputStream);
        } catch (PolicyException e) {
            log.error("read XSS config file [" + ANTISAMY_SLASHDOT_XML + "] fail , reason:", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("close XSS config file [" + ANTISAMY_SLASHDOT_XML + "] fail , reason:", e);
                }
            }
        }
    }

    /**
     * 跨站攻击语句过滤 方法
     *
     * @param paramValue           待过滤的参数
     * @param ignoreParamValueList 忽略过滤的参数列表
     * @return 清理后的字符串
     */
    public static String xssClean(String paramValue, List<String> ignoreParamValueList) {
        AntiSamy antiSamy = new AntiSamy();

        try {
            if (isIgnoreParamValue(paramValue, ignoreParamValueList)) {
                return paramValue;
            } else {
                final CleanResults cr = antiSamy.scan(paramValue, policy);
                cr.getErrorMessages().forEach(log::debug);
                String str = cr.getCleanHTML();
                str = str.replaceAll(StrPool.HTML_QUOTE, "\"");
                str = str.replaceAll(StrPool.HTML_AMP, "&");
                str = str.replaceAll(StrPool.SINGLE_QUOTE, "'");
                str = str.replaceAll(StrPool.SINGLE_QUOTE, "＇");

                str = str.replaceAll(StrPool.HTML_LT, "<");
                str = str.replaceAll(StrPool.HTML_GT, ">");
                return str;
            }
        } catch (ScanException e) {
            log.error("scan failed  is [" + paramValue + "]", e);
        } catch (PolicyException e) {
            log.error("antisamy convert failed  is [" + paramValue + "]", e);
        }
        return paramValue;
    }

    private static boolean isIgnoreParamValue(String paramValue, List<String> ignoreParamValueList) {
        if (StrUtil.isBlank(paramValue)) {
            return true;
        }
        if (CollectionUtil.isEmpty(ignoreParamValueList)) {
            return false;
        }
        for (String ignoreParamValue : ignoreParamValueList) {
            if (paramValue.contains(ignoreParamValue)) {
                return true;
            }
        }

        return false;
    }

}
