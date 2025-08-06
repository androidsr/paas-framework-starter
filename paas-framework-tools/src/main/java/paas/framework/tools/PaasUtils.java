package paas.framework.tools;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import paas.framework.snowflake.PaasIdWorker;

import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PaasUtils {
    public static int BATCH_ADD_SIZE = 500;
    private static final ExpressionParser parser = new SpelExpressionParser();

    public static boolean equals(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equalsIgnoreCase(str2);
    }

    public static boolean equalsIgnoreNull(String str1, String str2) {
        return Objects.equals(str1, str2);
    }

    public static boolean equalsIgnoreNullCase(String str1, String str2) {
        if (equalsIgnoreNull(str1, str2)) {
            return str1.equalsIgnoreCase(str2);
        }
        return false;
    }

    public static boolean equals(Number val1, Number val2) {
        if (val1 == null || val2 == null) {
            return false;
        }
        return val1.equals(val2);
    }

    public static boolean equalsIgnoreNull(Number str1, Number str2) {
        return Objects.equals(str1, str2);
    }

    public static boolean isEmpty(String str) {
        return !StringUtils.hasText(str);
    }

    public static boolean isNotEmpty(String str) {
        return StringUtils.hasText(str);
    }

    public static boolean isEmpty(Serializable str) {
        if (str == null || "".equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotEmpty(Serializable str) {
        if (str == null || "".equals(str)) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isEmpty(Number number) {
        return number == null;
    }

    public static boolean isNotEmpty(Number number) {
        return number != null;
    }

    public static boolean isEmpty(Collection list) {
        return CollectionUtils.isEmpty(list);
    }

    public static boolean isNotEmpty(Collection list) {
        return !CollectionUtils.isEmpty(list);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return map != null && !map.isEmpty();
    }

    public static <T> List<List<T>> splitList(List<T> data, int size) {
        List<List<T>> result = new ArrayList<>();
        int fromIndex;
        int toIndex;
        if (data.size() <= size && data.size() > 0) {
            result.add(data);
        } else {
            int n = data.size() / size;
            if (data.size() % size != 0) {
                n += 1;
            }
            for (int i = 0; i < n; i++) {
                fromIndex = i * size;
                toIndex = (i + 1) * size;
                if (toIndex > data.size()) {
                    toIndex = data.size();
                }
                result.add(data.subList(fromIndex, toIndex));
            }
        }
        return result;
    }


    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 不copy null值复制对象
     *
     * @param src    源对象
     * @param target 结果对象
     */
    public static void copyIgnoreNull(Object src, Object target) {
        BeanUtils.copyProperties(src, target, getNullPropertyNames(src));
    }

    /**
     * copy 复制对象
     *
     * @param src    源对象
     * @param target 结果对象
     */
    public static void copy(Object src, Object target) {
        BeanUtils.copyProperties(src, target);
    }

    /**
     * copy 复制对象
     *
     * @param src            源对象
     * @param targetSupplier 结果对象类型
     * @return 结果对象
     */
    public static <T> T copy(Object src, Supplier<T> targetSupplier) {
        if (null == src || null == targetSupplier) {
            return null;
        }
        T target = targetSupplier.get();
        copy(src, target);
        return target;
    }

    /**
     * copy List集合对象
     *
     * @param sources        源对象集合
     * @param targetSupplier 结果对象创建
     * @param <S>            源对象类型
     * @param <T>            结果对象类型
     * @return
     */
    public static <S, T> List<T> copyListTo(Collection<S> sources, Supplier<T> targetSupplier) {
        if (null == sources || null == targetSupplier) {
            return null;
        }
        List<T> list = new ArrayList<>(sources.size());
        for (S s : sources) {
            T target = targetSupplier.get();
            copy(s, target);
            list.add(target);
        }
        return list;
    }

    /**
     * 将nulll转换空串
     *
     * @param o
     * @return
     */
    public static String nullToBlank(Object o) {
        if (o != null) {
            return o.toString();
        } else {
            return "";
        }
    }

    /**
     * List取差级
     *
     * @param bigList
     * @param smallList
     * @return
     */
    public static Set diffList(Set bigList, Set smallList) {
        return (Set) bigList.parallelStream().filter(v -> !smallList.contains(v)).collect(Collectors.toSet());
    }

    /**
     * null转空对象
     *
     * @param value          判断值
     * @param targetSupplier 空对象
     * @return 转换后的值
     */
    public static <T> T ofNullable(T value, Supplier<T> targetSupplier) {
        if (value == null) {
            value = targetSupplier.get();
        }
        return value;
    }

    /**
     * 下划线转陀峰
     *
     * @param column 列信息
     * @return
     */
    public static String columnToField(String column) {
        if (PaasUtils.isEmpty(column)) {
            return column;
        }
        String[] split = column.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                sb.append(split[0].toLowerCase());
            } else {
                sb.append((String.valueOf(split[i].charAt(0))).toUpperCase()).append(split[i].substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 陀峰转下划线
     *
     * @param column 列信息
     * @return
     */
    public static String fieldToColumn(String column) {
        if (PaasUtils.isEmpty(column)) {
            return column;
        }
        StringBuilder sb = new StringBuilder();
        char[] charArray = column.toCharArray();
        for (char c : charArray) {
            if (Character.isUpperCase(c)) {
                sb.append('_');
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 获取雪花算法ID
     *
     * @return
     */
    public static Long getId() {
        return PaasIdWorker.nextId();
    }

    /**
     * 批量获取雪花算法ID
     *
     * @param size 大小
     * @return
     */
    public static List<Long> getListId(int size) {
        return PaasIdWorker.getBatchIds(size);
    }

    /**
     * 判断是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean containsChinese(String str) {
        String regex = ".*[\\u4e00-\\u9fa5].*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    public static Object getNestedValue(Map<String, Object> map, String key) {
        if (map == null || key == null || key.isEmpty()) {
            return null;
        }
        if (!key.contains(".")) {
            return map.get(key);
        }
        String[] keys = key.split("\\."); // 按 "." 分割路径
        Object value = map;

        for (String k : keys) {
            if (value instanceof Map) {
                value = ((Map<?, ?>) value).get(k); // 逐层解析
            } else {
                return null;
            }
        }
        return value;
    }

    private static final ParserContext parserContext = new ParserContext() {
        @Override
        public String getExpressionPrefix() {
            return "${";
        }

        @Override
        public String getExpressionSuffix() {
            return "}";
        }

        @Override
        public boolean isTemplate() {
            return true;
        }
    };

    public static String resolveTemplate(String content, Map<String, Object> variables) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.addPropertyAccessor(new MapAccessor());
        if (variables == null) {
            return content;
        }
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }

        Expression exp = parser.parseExpression(content, parserContext);
        return exp.getValue(context, String.class);
    }

}
