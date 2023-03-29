package com.github.sparkzxl.sms.parser;

import java.util.Map;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

/**
 * description: 模板参数解析
 *
 * @author zhouxinlei
 * @since 2022-05-26 10:30:31
 */
public class TemplateParamParser {

    private static final String DOLLAR_LEFT_BRACE = "${";

    private static final String RIGHT_BRACE = "}";

    public static String replaceContent(String content, Map<String, Object> params) {
        ExpressionParser parser = new SpelExpressionParser();
        TemplateParserContext parserContext = new TemplateParserContext(DOLLAR_LEFT_BRACE, RIGHT_BRACE);
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(params);
        evaluationContext.addPropertyAccessor(new MapPropertyAccessor());
        return parser.parseExpression(content, parserContext).getValue(evaluationContext, String.class);
    }

    static class MapPropertyAccessor implements PropertyAccessor {

        @Override
        public Class<?>[] getSpecificTargetClasses() {
            return new Class[]{Map.class};
        }

        @Override
        public boolean canRead(EvaluationContext context, Object target, String name) {
            return (target instanceof Map && ((Map<?, ?>) target).containsKey(name));
        }

        @Override
        public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
            Assert.state(target instanceof Map, "参数不是Map类型");
            Map<?, ?> map = (Map<?, ?>) target;
            if (!map.containsKey(name)) {
                throw new AccessException("Map中未包含该key: " + name);
            }
            Object value = map.get(name);
            return new TypedValue(value);
        }

        @Override
        public boolean canWrite(EvaluationContext context, Object target, String name) {
            return false;
        }

        @Override
        public void write(EvaluationContext context, Object target, String name, Object newValue) {

        }
    }
}
