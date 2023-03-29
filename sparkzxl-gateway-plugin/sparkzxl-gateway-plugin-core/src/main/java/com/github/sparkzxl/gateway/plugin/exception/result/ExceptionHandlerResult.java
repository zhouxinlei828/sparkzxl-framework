package com.github.sparkzxl.gateway.plugin.exception.result;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

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
