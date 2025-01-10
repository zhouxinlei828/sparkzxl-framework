package com.github.sparkzxl.core.parser;

import org.springframework.expression.*;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * description: 模板表达式解析
 *
 * @author zhouxinlei
 * @since 2022-05-26 10:30:31
 */
public class TemplateExpressionParser {

    private static final ExpressionParser PARSER;
    private static final TemplateParserContext PARSER_CONTEXT;

    static {
        PARSER = new SpelExpressionParser();
        PARSER_CONTEXT = new TemplateParserContext("${", "}");
    }

    public static String parseExpression(String content, Map<String, Object> params) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(params);
        evaluationContext.addPropertyAccessor(new MapPropertyAccessor());
        return PARSER.parseExpression(content, PARSER_CONTEXT).getValue(evaluationContext, String.class);
    }

    public static <T> T parseExpression(String content, Map<String, Object> params, Class<T> desiredResultType) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(params);
        evaluationContext.addPropertyAccessor(new MapPropertyAccessor());
        return PARSER.parseExpression(content, PARSER_CONTEXT).getValue(evaluationContext, desiredResultType);
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
