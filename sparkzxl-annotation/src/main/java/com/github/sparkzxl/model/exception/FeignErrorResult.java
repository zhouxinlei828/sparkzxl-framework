package com.github.sparkzxl.model.exception;

import cn.hutool.http.HttpStatus;
import lombok.Data;

import java.util.List;

@Data
public class FeignErrorResult {
    private int code;
    private boolean success;
    private String msg;
    private List<ExceptionChain> exceptionChains;

    /**
     * 返回结果
     *
     * @param code 状态码
     * @param msg  信息
     * @return FeignResult
     */
    public static FeignErrorResult feignErrorResult(int code, String msg, List<ExceptionChain> exceptionChains) {
        FeignErrorResult feignErrorResult = new FeignErrorResult();
        feignErrorResult.setCode(code);
        feignErrorResult.setMsg(msg);
        feignErrorResult.setExceptionChains(exceptionChains);
        feignErrorResult.setSuccess(code == HttpStatus.HTTP_OK);
        return feignErrorResult;
    }

}
