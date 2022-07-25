package com.github.sparkzxl.monitor.constant;

/**
 * description: 拦截器类型
 *
 * @author zhouxinlei
 * @since 2022-07-25 15:43:38
 */
public enum InterceptorType {

    FEIGN(MonitorConstant.FEIGN),
    REST_TEMPLATE(MonitorConstant.REST_TEMPLATE),
    WEB_CLIENT(MonitorConstant.WEB_CLIENT);

    private final String value;

    InterceptorType(String value) {
        this.value = value;
    }

    public static InterceptorType fromString(String value) {
        for (InterceptorType type : InterceptorType.values()) {
            if (type.getValue().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new IllegalArgumentException("No matched type with value=" + value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}