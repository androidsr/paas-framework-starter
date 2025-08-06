package paas.framework.mybatis;

import com.baomidou.mybatisplus.extension.toolkit.SqlRunner;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import paas.framework.model.exception.BusException;
import paas.framework.tools.PaasUtils;

import java.lang.reflect.Field;
import java.util.*;

public class DbToNameUtils {

    public static <T> void getDbToNameValue(T data) {
        getDbToNameValue(Arrays.asList(data));
    }

    public static <T> void getDbToNameValue(List<T> data) {
        try {
            if (PaasUtils.isEmpty(data)) {
                return;
            }
            List<DbToNameItem> dbFieldMap = new ArrayList<>();
            List<Field> allFields = FieldUtils.getFieldsListWithAnnotation(data.get(0).getClass(), DbToName.class);
            for (Field field : allFields) {
                field.setAccessible(true);
                DbToName annotation = field.getAnnotation(DbToName.class);
                DbToNameItem fieldItem = new DbToNameItem();
                fieldItem.setAnnotation(annotation);
                fieldItem.setField(field);
                for (T item : data) {
                    Object value = ReflectionUtils.getField(field, item);
                    if (ObjectUtils.isEmpty(value)) {
                        continue;
                    }
                    fieldItem.addValue(String.valueOf(value));
                }
                dbFieldMap.add(fieldItem);
            }

            for (DbToNameItem info : dbFieldMap) {
                String sql = String.format("select %s,%s from %s where %s in (" + genInStr(info.getValues().size()) + ")",
                        info.getAnnotation().id(), info.getAnnotation().name(), info.getAnnotation().table(),
                        info.getAnnotation().id());
                if (info.getValues().size() == 0) {
                    continue;
                }
                List<Map<String, Object>> maps = SqlRunner.db().selectList(sql, info.getValues().toArray());
                Map<String, String> dataMap = new HashMap<>();
                for (Map<String, Object> item : maps) {
                    String name = info.getAnnotation().name();
                    if (PaasUtils.isNotEmpty(info.getAnnotation().alias())) {
                        name = info.getAnnotation().alias();
                    }
                    dataMap.put(PaasUtils.nullToBlank(item.get(info.getAnnotation().id())), PaasUtils.nullToBlank(item.get(name)));
                }
                setAttributeValues(data, info.getField(), dataMap);
            }
        } catch (Exception e) {
            e.printStackTrace();
            BusException.fail(50000, e.getMessage());
        }

    }

    /**
     * 设置指定属性值
     *
     * @param data    数据集合
     * @param field   对象属性
     * @param dataMap 新值map
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    private static <T> void setAttributeValues(List<T> data, Field field, Map<String, String> dataMap) {
        try {
            if (dataMap == null) {
                return;
            }
            for (T item : data) {
                Object value = ReflectionUtils.getField(field, item);
                if (ObjectUtils.isEmpty(value)) {
                    continue;
                }
                Field setField = FieldUtils.getDeclaredField(data.get(0).getClass(), field.getName() + "Name", true);
                if (setField == null) {
                    return;
                }
                ReflectionUtils.setField(setField, item, dataMap.get(String.valueOf(value)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            BusException.fail(50000, e.getMessage());
        }
    }

    private static String genInStr(int size) {
        StringJoiner sj = new StringJoiner(",");
        for (int i = 0; i < size; i++) {
            sj.add("{" + i + "}");
        }
        return sj.toString();
    }
}
