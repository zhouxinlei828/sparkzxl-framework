package com.github.sparkzxl.dubbo.validation;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.validation.Validator;
import org.apache.dubbo.validation.support.AbstractValidation;

/**
 * description: 个性化校验
 *
 * @author zhouxinlei
 * @since 2022-08-11 13:58:39
 */
public class CustomValidation extends AbstractValidation {

    /**
     * Return new instance of {@link CustomValidator}
     *
     * @param url Valid URL instance
     * @return Instance of JValidator
     */
    @Override
    protected Validator createValidator(URL url) {
        return new CustomValidator(url);
    }
}
