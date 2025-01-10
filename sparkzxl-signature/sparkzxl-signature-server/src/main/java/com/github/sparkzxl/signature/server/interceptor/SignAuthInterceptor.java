package com.github.sparkzxl.signature.server.interceptor;

import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.signature.server.method.SignProcessor;
import com.google.common.collect.Maps;
import com.github.sparkzxl.core.support.ArgumentException;
import com.github.sparkzxl.signature.constant.SignatureConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;

public class SignAuthInterceptor implements AsyncHandlerInterceptor {

    @Autowired
    private SignProcessor signProcessor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取appKey
        String appKey = request.getHeader(SignatureConstant.APP_KEY);
        // 获取时间戳
        String timestamp = request.getHeader(SignatureConstant.TIMESTAMP);
        // 获取随机字符串
        String nonce = request.getHeader(SignatureConstant.NONCE);
        // 获取签名
        String signature = request.getHeader(SignatureConstant.SIGNATURE);


        Map<String, Object> params = Maps.newConcurrentMap();
        Enumeration<String> enumeration = request.getParameterNames();
        if (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getParameter(name);
            params.put(name, URLEncoder.encode(value, StandardCharsets.UTF_8.name()));
        }
        // 对请求头参数进行签名
        if (StrUtil.isEmpty(signature) || !signProcessor.verifySign(appKey, timestamp, nonce, signature, params)) {
            throw new ArgumentException("验签失败");
        }
        return true;
    }
}
