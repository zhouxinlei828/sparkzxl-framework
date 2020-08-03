package com.sparksys.patterns.strategy;

import com.sparksys.patterns.annonation.HandlerType;

import java.lang.annotation.Annotation;

/**
 * description: 策略模式业务类型注解实现类
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 15:07:01
 */
public class HandlerTypeImpl implements HandlerType {

    private final String type;
    private final String source;

    HandlerTypeImpl(String type, String source) {
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
        return HandlerType.class;
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
        if (!(obj instanceof HandlerType)) {
            return false;
        }
        HandlerType other = (HandlerType) obj;
        return type.equals(other.type()) && source.equals(other.source());
    }
}
