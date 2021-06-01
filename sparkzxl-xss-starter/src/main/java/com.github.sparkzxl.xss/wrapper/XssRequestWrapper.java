package com.github.sparkzxl.xss.wrapper;

import com.github.sparkzxl.core.utils.StrPool;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.List;
import java.util.Map;

import static com.github.sparkzxl.xss.utils.XssUtils.xssClean;


/**
 * description: 跨站攻击请求包装器
 *
 * @author zhouxinlei
 * @date 2021-05-31 14:03:13
 */
@Slf4j
public class XssRequestWrapper extends HttpServletRequestWrapper {

    private final List<String> ignoreParamValueList;

    public XssRequestWrapper(HttpServletRequest request, List<String> ignoreParamValueList) {
        super(request);
        this.ignoreParamValueList = ignoreParamValueList;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> requestMap = super.getParameterMap();
        for (Map.Entry<String, String[]> me : requestMap.entrySet()) {
            log.debug(me.getKey() + StrPool.COLON);
            String[] values = me.getValue();
            for (int i = 0; i < values.length; i++) {
                log.debug(values[i]);
                values[i] = xssClean(values[i], this.ignoreParamValueList);
            }
        }
        return requestMap;
    }

    @Override
    public String[] getParameterValues(String paramString) {
        String[] parameterValues = super.getParameterValues(paramString);
        if (parameterValues == null) {
            return null;
        }
        int i = parameterValues.length;
        String[] resultArray = new String[i];
        for (int j = 0; j < i; j++) {
            resultArray[j] = xssClean(parameterValues[j], this.ignoreParamValueList);
        }
        return resultArray;
    }

    @Override
    public String getParameter(String paramString) {
        String str = super.getParameter(paramString);
        if (str == null) {
            return null;
        }
        return xssClean(str, this.ignoreParamValueList);
    }

    @Override
    public String getHeader(String paramString) {
        String str = super.getHeader(paramString);
        if (str == null) {
            return null;
        }
        return xssClean(str, this.ignoreParamValueList);
    }


}
