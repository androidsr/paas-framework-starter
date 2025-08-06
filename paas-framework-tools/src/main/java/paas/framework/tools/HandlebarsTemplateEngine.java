package paas.framework.tools;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HandlebarsTemplateEngine {

    private final Handlebars handlebars;

    public HandlebarsTemplateEngine() {
        this.handlebars = new Handlebars();
        registerDefaultHelpers();
    }

    /**
     * 渲染模板
     *
     * @param templateStr Handlebars 模板字符串
     * @param data        模板上下文数据（Map / List）
     * @return 渲染结果
     */
    public String render(String templateStr, Object data) throws IOException {
        Template template = handlebars.compileInline(templateStr);
        return template.apply(data);
    }

    private void registerDefaultHelpers() {
        // 支持：{{get this "字段名"}}
        handlebars.registerHelper("get", (Helper<Object>) (context, options) -> {
            if (!(context instanceof Map<?, ?> map)) return "";
            String key = options.param(0, "");
            Object val = map.get(key);
            return val != null ? val : "";
        });

        // 支持嵌套字段：{{getNested this "a.b.c"}}
        handlebars.registerHelper("getNested", (Helper<Object>) (context, options) -> {
            if (!(context instanceof Map<?, ?> current)) return "";
            String expr = options.param(0, "");
            String[] parts = expr.split("\\.");
            Object value = current;
            for (String part : parts) {
                if (value instanceof Map<?, ?> map) {
                    value = map.get(part);
                } else {
                    return "";
                }
            }
            return value != null ? value : "";
        });

        // 数字保留小数位：{{toFixed 123.456 2}}
        handlebars.registerHelper("toFixed", (Helper<Object>) (value, options) -> {
            int digits = options.param(0, 2);
            if (value instanceof Number number) {
                String format = "%." + digits + "f";
                return String.format(Locale.ROOT, format, number.doubleValue());
            }
            return value != null ? value.toString() : "";
        });

        // 数字格式化：{{format 123.456 "0.00"}}
        handlebars.registerHelper("format", (Helper<Object>) (value, options) -> {
            String pattern = options.param(0, "0.00");
            if (value instanceof Number number) {
                try {
                    return new DecimalFormat(pattern).format(number);
                } catch (IllegalArgumentException e) {
                    return number.toString();
                }
            }
            return value != null ? value.toString() : "";
        });

        // 等于：{{#if (eq a b)}}
        handlebars.registerHelper("eq", (value, options) -> {
            Object other = options.param(0);
            if (value == null && other == null) return true;
            if (value == null || other == null) return false;
            if (value instanceof Number && other instanceof Number) {
                return ((Number) value).doubleValue() == ((Number) other).doubleValue();
            }
            return value.toString().equals(other.toString());
        });

        // 不等于：{{#if (ne a b)}}
        handlebars.registerHelper("ne", (value, options) -> {
            Object other = options.param(0);
            return !Objects.equals(value, other);
        });

        // 大于：{{#if (gt a b)}}
        handlebars.registerHelper("gt", (value, options) -> {
            if (value instanceof Number && options.param(0) instanceof Number) {
                return ((Number) value).doubleValue() > ((Number) options.param(0)).doubleValue();
            }
            return false;
        });

        // 小于：{{#if (lt a b)}}
        handlebars.registerHelper("lt", (value, options) -> {
            if (value instanceof Number && options.param(0) instanceof Number) {
                return ((Number) value).doubleValue() < ((Number) options.param(0)).doubleValue();
            }
            return false;
        });

        // 大于等于：{{#if (gte a b)}}
        handlebars.registerHelper("gte", (value, options) -> {
            if (value instanceof Number && options.param(0) instanceof Number) {
                return ((Number) value).doubleValue() >= ((Number) options.param(0)).doubleValue();
            }
            return false;
        });

        // 小于等于：{{#if (lte a b)}}
        handlebars.registerHelper("lte", (value, options) -> {
            if (value instanceof Number && options.param(0) instanceof Number) {
                return ((Number) value).doubleValue() <= ((Number) options.param(0)).doubleValue();
            }
            return false;
        });

        // 逻辑与：{{#if (and a b c)}}
        handlebars.registerHelper("and", (context, options) -> {
            for (Object param : options.params) {
                if (param == null || param.equals(false)) {
                    return false;
                }
            }
            return true;
        });

        // 逻辑或：{{#if (or a b c)}}
        handlebars.registerHelper("or", (context, options) -> {
            for (Object param : options.params) {
                if (param != null && !param.equals(false)) {
                    return true;
                }
            }
            return false;
        });

        // 逻辑非：{{#if (not a)}}
        handlebars.registerHelper("not", (context, options) -> {
            return context == null || context.equals(false);
        });
    }

}
