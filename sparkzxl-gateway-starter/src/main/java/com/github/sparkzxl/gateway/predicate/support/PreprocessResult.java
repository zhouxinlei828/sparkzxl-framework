package com.github.sparkzxl.gateway.predicate.support;

import lombok.*;

/**
 * description: Pre process ServerWebExchange Result
 *
 * @author zhoux
 * @date 2021-10-23 21:32:50
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
