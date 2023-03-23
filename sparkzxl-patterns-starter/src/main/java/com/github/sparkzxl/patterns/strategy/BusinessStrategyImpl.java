package com.github.sparkzxl.patterns.strategy;

import java.lang.annotation.Annotation;

/**
 * description: 策略模式业务类型注解实现类
 *
 * @author zhouxinlei
 */
public class BusinessStrategyImpl implements BusinessStrategy {

    private final String type;
    private final String source;

    public BusinessStrategyImpl(String type, String source) {
        this.source = source;
        this.type = type;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String source() {
        return source;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return BusinessStrategy.class;
    }

    @Override
    public int hashCode() {
        int hashCode = 0;
        hashCode += (127 * "type".hashCode()) ^ type.hashCode();
        hashCode += (127 * "source".hashCode()) ^ source.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BusinessStrategy)) {
            return false;
        }
        BusinessStrategy other = (BusinessStrategy) obj;
        return type.equals(other.type()) && source.equals(other.source());
    }
}
