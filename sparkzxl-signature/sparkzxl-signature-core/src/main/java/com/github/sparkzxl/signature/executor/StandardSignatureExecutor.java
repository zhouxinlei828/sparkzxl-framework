package com.github.sparkzxl.signature.executor;

import cn.hutool.core.util.IdUtil;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.signature.constant.SignatureConstant;
import com.github.sparkzxl.signature.constant.enums.SignTypeEnum;
import com.github.sparkzxl.signature.entity.SignResult;
import com.github.sparkzxl.signature.algorithm.SignAlgorithm;
import com.github.sparkzxl.signature.properties.SignatureProperties;

import java.util.Map;

/**
 * description: 通用签名执行器
 *
 * @author zhouxinlei
 * @since 2024-05-20 10:28:20
 */
public class StandardSignatureExecutor extends AbstractSignatureExecutor<Object> {

    @Override
    public SignResult sign(String appKey, Object data) {
        SignatureProperties.AppProperties properties = getConfigBySignAppId(appKey);
        Map<String, Object> paramMap = JsonUtils.getJson().toMap(data);
        long timestamp = System.currentTimeMillis();
        paramMap.computeIfAbsent(SignatureConstant.APP_KEY, k -> properties.getAppKey());
        paramMap.putIfAbsent(SignatureConstant.TIMESTAMP, timestamp);
        String nonce = IdUtil.fastSimpleUUID();
        paramMap.computeIfAbsent(SignatureConstant.NONCE, k -> nonce);
        SignAlgorithm signAlgorithm = getSignAlgorithm(properties.getAlgorithm());
        return signAlgorithm.sign(paramMap, timestamp, nonce, properties.getAppSecret());
    }

    @Override
    public boolean verify(String appKey, Long timestamp, String nonce, String sign, Object data) {
        SignatureProperties.AppProperties properties = getConfigBySignAppId(appKey);
        Map<String, Object> paramMap = JsonUtils.getJson().toMap(data);
        paramMap.put(SignatureConstant.APP_KEY, properties.getAppKey());
        paramMap.put(SignatureConstant.TIMESTAMP, timestamp);
        paramMap.put(SignatureConstant.NONCE, nonce);
        SignAlgorithm signAlgorithm = getSignAlgorithm(properties.getAlgorithm());
        return signAlgorithm.verify(paramMap, properties.getAppSecret(), sign);
    }

    @Override
    public String getType() {
        return SignTypeEnum.STANDARD_SIGNATURE.name();
    }
}
