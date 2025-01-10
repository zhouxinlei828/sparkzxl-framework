package com.github.sparkzxl.signature.executor;

import com.github.sparkzxl.core.util.ArgumentAssert;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2025-01-10 10:14:24
 */
@Component
public class SignatureExecutorContext {

    private final Map<String, SignatureExecutor> signAlgorithmMap = Maps.newHashMap();

    public SignatureExecutorContext(List<SignatureExecutor> signatureExecutorList) {
        if (CollectionUtils.isNotEmpty(signatureExecutorList)) {
            signatureExecutorList.forEach(signatureExecutor -> signAlgorithmMap.put(signatureExecutor.getType(), signatureExecutor));
        }
    }

    public SignatureExecutor getExecutor(String type) {
        SignatureExecutor signAlgorithm = signAlgorithmMap.get(type);
        ArgumentAssert.notNull(signAlgorithm, "未知签名算法");
        return signAlgorithm;
    }
}
