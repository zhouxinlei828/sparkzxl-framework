package com.github.sparkzxl.core.serializer;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.DateTimeException;
import java.util.Date;

/**
 * description: 自定义Date时间反序列化
 *
 * @author zhouxinlei
 */
public class CustomDateDeserializer extends JsonDeserializer<Date> {

    public static final String DEFAULT_DATE_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2}$";
    public static final String DEFAULT_DATE_TIME_FORMAT_MATCHES = "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";
    public static final String DEFAULT_DATE_FORMAT_EN_MATCHES = "^\\d{4}年\\d{1,2}月\\d{1,2}日$";
    public static final String DEFAULT_DATE_TIME_FORMAT_EN_MATCHES = "^\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒$";
    public static final String SLASH_DATE_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2}$";
    public static final String SLASH_DATE_TIME_FORMAT_MATCHES = "^\\d{4}/\\d{1,2}/\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";
    public static final String SLASH_DATE_FORMAT = "yyyy/MM/dd";
    public static final String SLASH_DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    /**
     * Specific format to use, if non-null; if null will
     * just use default format.
     */
    protected final FastDateFormat customFormat;

    /**
     * Let's also keep format String for reference, to use for error messages
     */
    protected final String formatString;

    public CustomDateDeserializer() {
        customFormat = null;
        formatString = null;
    }

    public CustomDateDeserializer(String formatStr) {
        customFormat = FastDateFormat.getInstance(formatStr);
        formatString = formatStr;
    }

    public CustomDateDeserializer(FastDateFormat dateFormat) {
        customFormat = dateFormat;
        formatString = dateFormat.getPattern();
    }

    @Override
    public Date deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        if (parser.hasToken(JsonToken.VALUE_STRING)) {
            String string = parser.getText().trim();
            if (StringUtils.isBlank(string)) {
                return null;
            }
            try {
                if (customFormat == null) {
                    return convert(string);
                }
                // JavaScript by default includes time and zone in JSON serialized Dates (UTC/ISO instant format).
                int length = 10;
                char match = 'T';
                if (string.length() > length && string.charAt(length) == match) {
                    return DateUtil.parseUTC(string);
                }
                synchronized (customFormat) {
                    try {
                        return DateUtil.parse(string, customFormat);
                    } catch (Exception e) {
                        return (Date) ctxt.handleWeirdStringValue(handledType(), string, "expected format \"%s\"", formatString);
                    }
                }
            } catch (DateTimeException e) {
                return (Date) ctxt.handleWeirdStringValue(handledType(), string, "expected format \"%s\"", formatString);
            }
        }
        if (parser.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return DateUtil.date(parser.getLongValue());
        }
        return null;
    }

    private Date convert(String source) {
        if (source.matches(DEFAULT_DATE_FORMAT_MATCHES)) {
            return DateUtil.parse(source, DatePattern.NORM_DATE_PATTERN);
        }
        if (source.matches(DEFAULT_DATE_FORMAT_EN_MATCHES)) {
            return DateUtil.parse(source, DatePattern.CHINESE_DATE_PATTERN);
        }
        if (source.matches(SLASH_DATE_FORMAT_MATCHES)) {
            return DateUtil.parse(source, SLASH_DATE_FORMAT);
        }
        if (source.matches(DEFAULT_DATE_TIME_FORMAT_MATCHES)) {
            return DateUtil.parse(source, DatePattern.NORM_DATETIME_PATTERN);
        }
        if (source.matches(DEFAULT_DATE_TIME_FORMAT_EN_MATCHES)) {
            return DateUtil.parse(source, DatePattern.CHINESE_DATE_TIME_PATTERN);
        }
        if (source.matches(SLASH_DATE_TIME_FORMAT_MATCHES)) {
            return DateUtil.parse(source, SLASH_DATE_TIME_FORMAT);
        }
        return null;
    }

}
