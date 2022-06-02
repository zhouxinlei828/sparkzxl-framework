package com.github.sparkzxl.feign.exception;

import org.springframework.beans.BeanUtils;
import org.springframework.core.style.ToStringCreator;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-05-09 13:50:43
 */
public abstract class AbstractConfigurable<C> implements Configurable<C> {

    private final Class<C> configClass;

    protected AbstractConfigurable(Class<C> configClass) {
        this.configClass = configClass;
    }

    @Override
    public Class<C> getConfigClass() {
        return configClass;
    }

    @Override
    public C newConfig() {
        return BeanUtils.instantiateClass(this.configClass);
    }

    @Override
    public String toString() {
        return new ToStringCreator(this).append("configClass", configClass).toString();
    }
}
