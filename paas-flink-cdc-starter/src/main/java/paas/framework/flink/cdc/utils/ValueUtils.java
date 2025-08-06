package paas.framework.flink.cdc.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import paas.framework.tools.JSON;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ValueUtils {
    /**
     * 比如修改后的字段信息
     *
     * @param afterData  更新后的对象
     * @param beforeData 更新前的对象
     * @return 更新了的信息
     */
    public static JsonNode diffJSONObjects(JsonNode afterData, JsonNode beforeData) {
        if (afterData == null) {
            return null;
        }
        if (beforeData == null) {
            return afterData;
        }

        ObjectNode diff = JSON.createObjectNode();

        // 获取 afterData 和 beforeData 中的字段名
        Iterator<String> afterKey = afterData.fieldNames();

        // 将 beforeData 的字段名存储到 Set 中以便检查
        Set<String> beforeKeys = new HashSet<>();
        Iterator<String> beforeKey = beforeData.fieldNames();
        while (beforeKey.hasNext()) {
            beforeKeys.add(beforeKey.next());
        }

        // 遍历 afterData 的字段，比较差异
        while (afterKey.hasNext()) {
            String fieldName = afterKey.next();

            JsonNode afterValue = afterData.get(fieldName);
            JsonNode beforeValue = beforeData.get(fieldName);

            // 如果 beforeData 中没有该字段（新增字段）
            if (!beforeKeys.contains(fieldName)) {
                diff.set(fieldName, afterValue);

                // 如果 afterData 中该字段为 null（删除字段）
            } else if (afterValue == null) {
                diff.set(fieldName, null);

                // 如果 afterData 中该字段与 beforeData 中的字段不相等（值发生变化）
            } else if (!afterValue.equals(beforeValue)) {
                diff.set(fieldName, afterValue);
            }
        }

        return diff;
    }
}
