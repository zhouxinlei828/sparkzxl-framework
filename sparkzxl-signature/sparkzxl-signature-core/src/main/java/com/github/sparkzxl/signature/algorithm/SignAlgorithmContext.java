package com.github.sparkzxl.signature.algorithm;

import com.github.sparkzxl.core.util.ArgumentAssert;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class SignAlgorithmContext {

    private final Map<String, SignAlgorithm> signAlgorithmMap = Maps.newHashMap();

    public SignAlgorithmContext(List<SignAlgorithm> signAlgorithms) {
        if (CollectionUtils.isNotEmpty(signAlgorithms)) {
            signAlgorithms.forEach(signAlgorithm -> signAlgorithmMap.put(signAlgorithm.getType(), signAlgorithm));
        }
    }

    public SignAlgorithm getSignAlgorithm(String type) {
        SignAlgorithm signAlgorithm = signAlgorithmMap.get(type);
        ArgumentAssert.notNull(signAlgorithm, "未知签名算法");
        return signAlgorithm;
    }
}
