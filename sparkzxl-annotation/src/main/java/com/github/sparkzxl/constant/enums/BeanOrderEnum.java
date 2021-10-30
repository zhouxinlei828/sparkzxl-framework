package com.github.sparkzxl.constant.enums;

/**
 * description: The Order Of Bean
 *
 * @author zhoux
 * @date 2021-10-23 13:44:59
 */
public enum BeanOrderEnum {

    REGISTRY_FEIGN_FILTER(50),
    DATASOURCE_EXCEPTION_HANDLER_ORDER(Integer.MIN_VALUE),
    CACHE_EXCEPTION_ORDER(Integer.MIN_VALUE + 1),
    SENTINEL_EXCEPTION_ORDER(Integer.MIN_VALUE + 2),
    ZOOKEEPER_EXCEPTION_ORDER(Integer.MIN_VALUE + 3),
    FEIGN_EXCEPTION_ORDER(Integer.MIN_VALUE + 4),
    BASE_EXCEPTION_ORDER(Integer.MIN_VALUE + 5),
    APPLICATION_LOG_ORDER(Integer.MIN_VALUE + 1),
    ;

    private final int order;

    BeanOrderEnum(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }

}
