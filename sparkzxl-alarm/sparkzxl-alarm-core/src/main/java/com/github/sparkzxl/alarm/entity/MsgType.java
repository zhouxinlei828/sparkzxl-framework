package com.github.sparkzxl.alarm.entity;

import com.github.sparkzxl.alarm.enums.AlarmType;
import lombok.Data;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.Serializable;
import java.util.Map;

/**
 * description: 消息体类型
 *
 * @author zhouxinlei
 * @since 2022-05-18 10:14:51
 */
@Data
public class MsgType implements Serializable {

    private volatile AlarmType alarmType;

    private String msgtype;

    public void transfer(Map<String, Object> params) {

    }

    protected String replaceContent(String content, Map<String, Object> params) {
        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext parserContext = new TemplateParserContext();
        return parser.parseExpression(content, parserContext).getValue(params, String.class);
    }
}
