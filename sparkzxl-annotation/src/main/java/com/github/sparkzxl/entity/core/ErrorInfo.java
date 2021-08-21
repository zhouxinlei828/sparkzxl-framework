package com.github.sparkzxl.entity.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * description: 异常信息
 *
 * @author zhoux
 * @date 2021-08-21 11:34:12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class ErrorInfo {

    private String applicationName;

    private String traceId;

    private String message;

    private String throwableName;

    private String threadName;
    /**
     * 调用者的行号
     */
    private int lineNumber;
    /**
     * 调用者的文件名
     */
    private String fileName;
    /**
     * 调用者的完全限定类名
     */
    private String className;
    /**
     * 调用的方法名称
     */
    private String methodName;

}
