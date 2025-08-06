package paas.framework.mybatis.scope;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;
import paas.framework.tools.PaasUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 全局数据权限处理器
 */
public class ScopeHandler implements DataPermissionHandler {

    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        ScopeInfo config = ScopeContextHolder.getContext();
        if (config == null || CollUtil.isEmpty(config.getParams())) {
            return where;
        } else {
            return dataScopeFilter(where, config.getParams());
        }
    }

    public static Expression dataScopeFilter(Expression where, Map<String, Object> conditions) {
        for (String key : conditions.keySet()) {
            Object value = conditions.get(key);
            if (value instanceof Collection<?> collection) {
                if (PaasUtils.isEmpty(collection)) {
                    continue;
                }

                ExpressionList itemsList = new ExpressionList();
                itemsList.add("(''");
                itemsList.addAll(collection.stream().filter(Objects::nonNull)  // 过滤null值
                        .map(String::valueOf)       // 将元素转换为String
                        .map(StringValue::new)      // 创建SQL字符串值
                        .collect(Collectors.toList()));
                itemsList.add("'')");
                InExpression expression = new InExpression(new Column(key), itemsList);
                if (where == null) {
                    where = expression;
                } else {
                    where = new AndExpression(where, expression);
                }
            } else {
                if (PaasUtils.isEmpty(String.valueOf(value))) {
                    continue;
                }
                EqualsTo equalsTo = new EqualsTo(new Column(key), new StringValue(String.valueOf(value)));
                if (where == null) {
                    where = equalsTo;
                } else {
                    where = new AndExpression(where, equalsTo);
                }
            }
        }
        return where;
    }
}