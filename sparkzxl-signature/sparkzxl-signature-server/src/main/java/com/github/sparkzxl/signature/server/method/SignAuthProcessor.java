package com.github.sparkzxl.signature.server.method;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.core.support.ArgumentException;
import com.github.sparkzxl.core.util.ArgumentAssert;
import com.github.sparkzxl.signature.executor.SignatureExecutor;
import com.github.sparkzxl.signature.executor.SignatureExecutorContext;
import com.github.sparkzxl.signature.properties.SignatureProperties;
import com.github.sparkzxl.signature.server.cache.SignCache;
import com.github.sparkzxl.signature.server.properties.SignatureServerProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class SignAuthProcessor implements SignProcessor {

    @Autowired
    private SignatureExecutorContext signatureExecutorContext;
    @Autowired
    private SignCache signCache;
    @Autowired
    private SignatureServerProperties signatureServerProperties;
    @Autowired
    private SignatureProperties signatureProperties;

    @Override
    public boolean verifySign(String appKey, String timestamp, String nonce, String sign, Map<String, Object> params) {

        if (StrUtil.isEmpty(appKey)) {
            throw new ArgumentException("invalid sign appKey");
        }

        // 判断时间是否大于xx秒(防止重放攻击)
        long NONCE_STR_TIMEOUT_SECONDS = signatureServerProperties.getNonceTimeoutSeconds();
        if (StrUtil.isEmpty(timestamp) || DateUtil.between(DateUtil.date(Long.parseLong(timestamp) * 1000), DateUtil.date(), DateUnit.SECOND) > NONCE_STR_TIMEOUT_SECONDS) {
            throw new ArgumentException("invalid  timestamp");
        }

        // 判断该用户的nonce参数是否已经在redis中（防止短时间内的重放攻击）
        boolean haveNonce = signCache.containsKey(nonce);
        if (StrUtil.isEmpty(nonce) || haveNonce) {
            throw new ArgumentException("invalid nonce");
        }

        // 对请求头参数进行签名
        if (StrUtil.isEmpty(sign) || !this.verifySignature(sign, appKey, timestamp, nonce, params)) {
            throw new ArgumentException("验签失败");
        }

        // 将本次用户请求的nonceStr参数存到redis中设置xx秒后自动删除
        signCache.set(nonce, nonce, NONCE_STR_TIMEOUT_SECONDS);
        return true;
    }

    private boolean verifySignature(String sign, String appKey, String timestamp, String nonce, Map<String, Object> params) {
        Map<String, SignatureProperties.AppProperties> provider = signatureProperties.getProvider();
        SignatureProperties.AppProperties appProperties = provider.get(appKey);
        ArgumentAssert.notNull(appProperties, "签名应用程序Key[{}]签名配置不存在", appKey);
        SignatureExecutor signatureExecutor = signatureExecutorContext.getExecutor(appProperties.getSignType().name());
        return signatureExecutor.verify(appKey, Long.valueOf(timestamp), nonce, sign, params);
    }
}
