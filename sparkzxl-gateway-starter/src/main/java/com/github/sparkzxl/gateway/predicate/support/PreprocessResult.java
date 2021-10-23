package com.github.sparkzxl.gateway.predicate.support;

import lombok.*;

/**
 * Pre process ServerWebExchange Result
 *
 * @author chenggang
 * @date 2019/07/17
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
