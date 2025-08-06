package paas.framework.redis.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MapAccessor;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import paas.framework.tools.JSON;
import paas.framework.tools.PaasUtils;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.StringJoiner;

public class ExpressionParser {

    /**
     * 用于SpEL表达式解析.
     */
    private SpelExpressionParser parser = new SpelExpressionParser();
    /**
     * 用于获取方法参数定义名字.
     */
    private DefaultParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();

    public String generateKeyBySpEL(String spELString, ProceedingJoinPoint joinPoint) {
        if (!spELString.startsWith("#") || PaasUtils.isEmpty(spELString)) {
            return spELString;
        }
        // 通过joinPoint获取被注解方法
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        // 使用spring的DefaultParameterNameDiscoverer获取方法形参名数组
        String[] paramNames = nameDiscoverer.getParameterNames(method);
        // 解析过后的Spring表达式对象
        Expression expression = parser.parseExpression(spELString);
        // spring的表达式上下文对象
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.addPropertyAccessor(new MapAccessor());
        // 通过joinPoint获取被注解方法的形参
        Object[] args = joinPoint.getArgs();
        // 给上下文赋值
        if (paramNames != null) {
            for (int i = 0; i < Math.min(args.length, paramNames.length); i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        } else {
            String[] split = spELString.split(":");
            int i = 0;
            for (String key : split) {
                context.setVariable(key.replace("#", ""), args[i++]);
            }
        }
        Object value = expression.getValue(context);
        if (value == null) {
            return "";
        }
        return transition(value);
    }

    public String transition(Object value) {
        if (value instanceof Integer) {
            return String.valueOf(value);
        } else if (value instanceof String) {
            return String.valueOf(value);
        } else if (value instanceof Double) {
            return String.valueOf(value);
        } else if (value instanceof Float) {
            return String.valueOf(value);
        } else if (value instanceof Long) {
            return String.valueOf(value);
        } else if (value instanceof Boolean) {
            return String.valueOf(value);
        } else if (value instanceof Date) {
            return String.valueOf(value);
        } else if (value instanceof LocalDate) {
            return String.valueOf(value);
        } else if (value instanceof LocalDateTime) {
            return String.valueOf(value);
        } else if (value instanceof Collection) {
            return String.join("-", (Collection) value);
        } else if (value instanceof String[] || value instanceof Object[] || value instanceof Integer[] || value instanceof Long[]) {
            return String.join("-", (CharSequence[]) value);
        } else {
            Map map = JSON.parseObject(JSON.toJSONString(value), Map.class);
            StringJoiner stringJoiner = new StringJoiner("-");
            for (Object v : map.values()) {
                stringJoiner.add(String.valueOf(v));
            }
            return stringJoiner.toString();
        }
    }

}
