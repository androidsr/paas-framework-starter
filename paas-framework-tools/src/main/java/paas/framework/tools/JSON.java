package paas.framework.tools;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class JSON {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    /**
     * 将对象转换为 JSON 字符串
     */
    public static String toJSONString(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将对象转换为 JSON 字符串（支持漂亮打印）
     */
    public static String toJSONString(Object object, boolean prettyPrint) {
        if (object == null) {
            return null;
        }
        if (prettyPrint) {
            try {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return toJSONString(object);
        }
    }

    /**
     * 将 JSON 字符串转换为对象
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(text, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 泛型类型对象转换
     *
     * @param text
     * @param typeReference
     * @param <T>
     * @return
     */
    public static <T> T parse(String text, TypeReference<T> typeReference) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(text, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串转换为 Map
     */
    public static Map<String, Object> parseObject(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(text, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串转换为 List
     */
    public static List<Object> parseArray(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(text, List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串转换为 JsonNode（树形结构）
     */
    public static JsonNode parse(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readTree(text);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // ============================== JSON 类型转换 ==============================

    /**
     * 将 JSON 字符串转换为指定类型的对象
     */
    public static <T> T parseObject(String text, TypeFactory typeFactory) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(text, typeFactory.constructType(Object.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串转换为指定的 List 类型
     */
    public static <T> List<T> parseArray(String text, Class<T> elementClass) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <K, V> List<Map<K, V>> parseArrayMap(String text, Class<K> keyElementType, Class<V> valueElementType) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(text, new TypeReference<List<Map<K, V>>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将 JSON 字符串转换为指定的 Map 类型
     */
    public static <K, V> Map<K, V> parseMapObject(String text, Class<K> keyClass, Class<V> valueClass) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // 针对列表的解析方法
    public static <K, V> Map<K, List<V>> parseMapList(String text, Class<K> keyElementType, Class<V> valueElementType) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(text, new TypeReference<Map<K, List<V>>>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    // ============================== JsonNode 操作 ==============================

    /**
     * 将 JsonNode 转换为 JSON 字符串
     */
    public static String toJSONString(JsonNode node) {
        if (node == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取 JsonNode 中某个字段的值
     */
    public static String getString(JsonNode node, String field) {
        if (node != null && field != null) {
            JsonNode fieldNode = node.get(field);
            if (fieldNode != null) {
                return fieldNode.asText();
            }
        }
        return null;
    }

    /**
     * 判断 JsonNode 是否包含某个字段
     */
    public static boolean containsField(JsonNode node, String field) {
        return node != null && field != null && node.has(field);
    }

    /**
     * 获取 JsonNode 中某个字段的整数值
     */
    public static int getInt(JsonNode node, String field) {
        if (node != null && field != null) {
            JsonNode fieldNode = node.get(field);
            if (fieldNode != null && fieldNode.isInt()) {
                return fieldNode.asInt();
            }
        }
        return 0; // 默认值
    }

    /**
     * 获取 JsonNode 中某个字段的布尔值
     */
    public static boolean getBoolean(JsonNode node, String field) {
        if (node != null && field != null) {
            JsonNode fieldNode = node.get(field);
            if (fieldNode != null && fieldNode.isBoolean()) {
                return fieldNode.asBoolean();
            }
        }
        return false; // 默认值
    }

    /**
     * 获取 JsonNode 中某个字段的长整型值
     */
    public static long getLong(JsonNode node, String field) {
        if (node != null && field != null) {
            JsonNode fieldNode = node.get(field);
            if (fieldNode != null && fieldNode.isLong()) {
                return fieldNode.asLong();
            }
        }
        return 0L; // 默认值
    }

    // ============================== JSON 数组操作 ==============================

    /**
     * 获取 JSON 数组的大小
     */
    public static int getArraySize(JsonNode node) {
        if (node != null && node.isArray()) {
            return node.size();
        }
        return 0;
    }

    /**
     * 获取 JSON 数组中的某个元素
     */
    public static JsonNode getArrayElement(JsonNode node, int index) {
        if (node != null && node.isArray() && node.size() > index) {
            return node.get(index);
        }
        return null;
    }

    // ============================== 其他辅助方法 ==============================

    /**
     * 判断字符串是否是有效的 JSON 格式
     */
    public static boolean isValidJson(String text) {
        try {
            parse(text);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * 深度克隆对象
     */
    public static <T> T deepClone(T object, Class<T> clazz) throws IOException {
        String json = toJSONString(object);
        return parseObject(json, clazz);
    }


    /**
     * 合并两个 JSON 对象
     */
    public static JsonNode merge(JsonNode mainNode, JsonNode updateNode) {
        ((ObjectNode) mainNode).setAll((ObjectNode) updateNode);
        return mainNode;
    }

    /**
     * JSON 中的字段进行排序
     *
     * @param json
     * @return
     * @throws IOException
     */
    public static String sortFields(String json) {
        JsonNode node = null;
        try {
            node = objectMapper.readTree(json);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(node);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 创建一个ObjectNode对象
     *
     * @return
     */
    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    /**
     * 创建一个ObjectNode对象
     *
     * @return
     */
    public static ArrayNode createArrayNode() {
        return objectMapper.createArrayNode();
    }

    /**
     * HJSON 字符串 → 标准 JSON 字符串
     */
    public static String hjsonToJson(String hjson) {
        if (hjson == null || hjson.trim().isEmpty()) return null;
        try {
            JsonValue hjsonValue = JsonValue.readHjson(hjson);
            return hjsonValue.toString(Stringify.PLAIN); // 压缩格式
        } catch (Exception e) {
            throw new RuntimeException("HJSON 转 JSON 失败", e);
        }
    }

    /**
     * HJSON 字符串 → Jackson JsonNode
     */
    public static JsonNode hjsonToJsonNode(String hjson) {
        try {
            String json = hjsonToJson(hjson);
            return objectMapper.readTree(json);
        } catch (IOException e) {
            throw new RuntimeException("HJSON 转 JsonNode 失败", e);
        }
    }

    /**
     * 标准 JSON 字符串 → HJSON 字符串（带注释/可读性）
     */
    public static String jsonToHjson(String json) {
        if (json == null || json.trim().isEmpty()) return null;
        try {
            JsonValue jsonValue = JsonValue.readJSON(json);
            return jsonValue.toString(Stringify.HJSON); // HJSON 格式
        } catch (Exception e) {
            throw new RuntimeException("JSON 转 HJSON 失败", e);
        }
    }

    /**
     * 将对象序列化为 HJSON 字符串
     */
    public static String toHjson(Object obj) {
        try {
            String json = objectMapper.writeValueAsString(obj);
            return jsonToHjson(json);
        } catch (Exception e) {
            throw new RuntimeException("对象转 HJSON 失败", e);
        }
    }

    /**
     * 将 HJSON 解析为指定类型对象
     */
    public static <T> T fromHjson(String hjson, Class<T> clazz) {
        try {
            String json = hjsonToJson(hjson);
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("HJSON 转对象失败", e);
        }
    }
}
