package hhplus.concertreservationservice.global.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class CustomSpringELParser {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    private CustomSpringELParser() {}

    public static String parseKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        StandardEvaluationContext context = createContext(signature.getParameterNames(), joinPoint.getArgs());

        return PARSER.parseExpression(keyExpression).getValue(context, String.class);
    }

    private static StandardEvaluationContext createContext(String[] parameterNames, Object[] args) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return context;
    }
}