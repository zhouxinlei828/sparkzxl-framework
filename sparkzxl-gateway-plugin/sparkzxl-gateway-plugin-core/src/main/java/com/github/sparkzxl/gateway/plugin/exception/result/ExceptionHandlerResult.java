package com.github.sparkzxl.gateway.plugin.exception.result;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

/**
 * description: 异常处理结果
 *
 * @author zhouxinlei
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionHandlerResult implements Serializable {

    private static final long serialVersionUID = -7653355090889829663L;

    private HttpStatus httpStatus;

    private String responseResult;

}