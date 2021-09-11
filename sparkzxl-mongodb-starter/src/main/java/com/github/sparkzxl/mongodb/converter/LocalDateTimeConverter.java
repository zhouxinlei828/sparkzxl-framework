package com.github.sparkzxl.mongodb.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;

public class LocalDateTimeConverter implements Converter<LocalDateTime, LocalDateTime> {
    @Override
    public LocalDateTime convert(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.plusHours(8);
    }
}
