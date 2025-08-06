package paas.framework.mybatis.wrapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.util.ObjectUtils;
import paas.framework.model.model.Between;
import paas.framework.tools.PaasUtils;

import java.lang.reflect.Field;
import java.util.*;

public class SearchWrapperUtil {

    public static QueryWrapper toSearchWrapper(Object condition) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        Field[] fields = condition.getClass().getDeclaredFields();
        Field[] suFields = condition.getClass().getSuperclass().getDeclaredFields();
        List<Field> all = new ArrayList<>(fields.length + suFields.length);
        all.addAll(Arrays.asList(fields));
        all.addAll(Arrays.asList(suFields));
        for (Field field : all) {
            field.setAccessible(true);
            SearchWrapper wrapper = field.getAnnotation(SearchWrapper.class);
            try {
                if (Objects.nonNull(wrapper)) {
                    Object value = field.get(condition);
                    String column = wrapper.column();
                    if (!ObjectUtils.isEmpty(value)) {
                        if (PaasUtils.isEmpty(column)) {
                            column = PaasUtils.fieldToColumn(field.getName());
                        }
                        set(queryWrapper, wrapper.keyword(), column, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return queryWrapper;
    }

    public static void set(QueryWrapper queryWrapper, KeywordsEnum keyword, String column, Object value) {
        switch (keyword) {
            case eq:
                queryWrapper.eq(column, value);
                break;
            case allEq:
                if (value instanceof Map) {
                    queryWrapper.allEq((Map) value);
                }
                break;
            case ne:
                queryWrapper.ne(column, value);
                break;
            case in:
                queryWrapper.in(column, (Collection<?>) value);
                break;
            case notIn:
                queryWrapper.notIn(column, (Collection<?>) value);
                break;
            case gt:
                queryWrapper.gt(column, value);
                break;
            case lt:
                queryWrapper.lt(column, value);
                break;
            case ge:
                queryWrapper.ge(column, value);
                break;
            case le:
                queryWrapper.le(column, value);
                break;
            case between:
                if (value instanceof Between) {
                    Between between = (Between) value;
                    if (between.getStart() == null) {
                        between.setStart(between.getEnd());
                    }
                    if (between.getEnd() == null) {
                        between.setEnd(between.getStart());
                    }
                    if (between.getStart() == null || between.getEnd() == null) {
                        break;
                    }
                    queryWrapper.between(column, between.getStart(), between.getEnd());
                } else if (value instanceof List) {
                    List v = (List) value;
                    if (PaasUtils.isEmpty(v)) {
                        break;
                    }
                    if (v.size() == 1) {
                        queryWrapper.between(column, v.get(0), v.get(0));
                    } else {
                        queryWrapper.between(column, v.get(0), v.get(1));
                    }
                }
                break;
            case notBetween:
                if (value instanceof Between) {
                    Between between = (Between) value;
                    if (between.getStart() == null) {
                        between.setStart(between.getEnd());
                    }
                    if (between.getEnd() == null) {
                        between.setEnd(between.getStart());
                    }
                    if (between.getStart() == null || between.getEnd() == null) {
                        break;
                    }
                    queryWrapper.notBetween(column, between.getStart(), between.getEnd());
                } else if (value instanceof List) {
                    List v = (List) value;
                    if (PaasUtils.isEmpty(v)) {
                        break;
                    }
                    if (v.size() == 1) {
                        queryWrapper.notBetween(column, v.get(0), v.get(0));
                    } else {
                        queryWrapper.notBetween(column, v.get(0), v.get(1));
                    }
                }
                break;
            case like:
                queryWrapper.like(column, value);
                break;
            case notLike:
                queryWrapper.notLike(column, value);
                break;
            case likeLeft:
                queryWrapper.likeLeft(column, value);
                break;
            case likeRight:
                queryWrapper.likeRight(column, value);
                break;
            case and:
                if (value instanceof List) {
                    queryWrapper.apply("and (" + column + ")", ((List<?>) value).toArray());
                }
                break;
            case or:
                if (value instanceof List) {
                    queryWrapper.apply("or (" + column + ")", ((List<?>) value).toArray());
                }
                break;
        }
    }
}