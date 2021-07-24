package com.github.sparkzxl.core.encrypt;

import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.utils.HuSecretUtils;

/**
 * description: md5 加密
 *
 * @author zhouxinlei
 * @date 2021-06-25 22:37:38
 */
public class DefaultEncryptFormatter implements EncryptFormatter {

    @Override
    public String encrypted(Object source) {
        String json = JsonUtil.toJson(source);
        return HuSecretUtils.encryptMd5(json);
    }
}