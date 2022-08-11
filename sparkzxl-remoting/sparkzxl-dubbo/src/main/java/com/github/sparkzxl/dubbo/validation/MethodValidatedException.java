package com.github.sparkzxl.dubbo.validation;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 方法校验异常
 *
 * @author zhouxinlei
 * @since 2022-08-11 10:54:20
 */
public class MethodValidatedException extends RuntimeException {

    private static final long serialVersionUID = 3588016356573293333L;
    private final List<ValidationResult> validationResults;

    public MethodValidatedException(List<ValidationResult> validationResults) {
        this.validationResults = validationResults;
    }

    public MethodValidatedException(String message) {
        super(message);
        this.validationResults = new ArrayList<>();
    }

    public MethodValidatedException(String message, List<ValidationResult> validationResults) {
        super(message);
        this.validationResults = validationResults;

    }

    public List<ValidationResult> getValidationResults() {
        return validationResults;
    }
}
