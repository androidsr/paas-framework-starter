package paas.framework.tools;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

import java.util.List;
import java.util.Map;

public class ExpressionUtils {
    /**
     * 表达式计算
     *
     * @param map
     * @param expression
     * @return
     */
    public static boolean execute(Map<String, Object> map, String expression) {
        Expression exp = AviatorEvaluator.compile(expression.substring(2, expression.length() - 1));
        List<String> variableNames = exp.getVariableNames();
        for (String variableName : variableNames) {
            if (!map.containsKey(variableName)) {
                return false;
            }
        }
        final Object execute = exp.execute(map);
        return Boolean.parseBoolean(String.valueOf(execute));
    }
}
