package com.github.sparkzxl.core.base.result;

/**
 * description: 响应状态码常量
 *
 * @author zhouxinlei
 * @date 2022-01-06 17:11:51
 */
public interface ExceptionStatusConstant {

    String OK = "200";
    String SYSTEM_ERROR = "B0001";

    String HTTP_UNAUTHORIZED = "401";

    String HTTP_FORBIDDEN = "403";

    String HTTP_NOT_FOUND = "404";

    String HTTP_BAD_METHOD = "405";

    String HTTP_UNSUPPORTED_TYPE = "415";

    String HTTP_INTERNAL_ERROR = "500";

    String HTTP_UNAVAILABLE = "503";

    String SIGNATURE_EXCEPTION_STATUS = "A0340";
}
