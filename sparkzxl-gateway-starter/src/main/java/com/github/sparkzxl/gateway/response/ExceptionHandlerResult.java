package com.github.sparkzxl.gateway.response;

import lombok.*;
import org.springframework.http.HttpStatus;

/**
 * description: 异常处理结果
 *
 * @author zhoux
 * @date 2021-10-23 16:32:25
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionHandlerResult {

    private HttpStatus httpStatus;

    private String responseResult;

}
