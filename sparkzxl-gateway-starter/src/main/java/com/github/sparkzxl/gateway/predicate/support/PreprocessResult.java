package com.github.sparkzxl.gateway.predicate.support;

import lombok.*;

/**
 * description: Pre process ServerWebExchange Result
 *
 * @author zhoux
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class PreprocessResult<T> {

    private Boolean result;
    private T resultData;

}
