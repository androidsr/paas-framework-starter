package paas.framework.web.formatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.ObjectUtils;
import paas.framework.tools.PaasUtils;
import paas.framework.web.SpringUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.StringJoiner;

public class StringToNameSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private String key;
    private String setField;

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                StringToName annotation = beanProperty.getAnnotation((StringToName.class));
                if (annotation == null) {
                    annotation = beanProperty.getContextAnnotation(StringToName.class);
                }
                StringToNameSerializer serializer = new StringToNameSerializer();
                if (annotation != null) {
                    serializer.key = annotation.key();
                    serializer.setField = annotation.setField();
                }
                return serializer;
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }

    @Override
    public void serialize(String obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (obj == null) {
            return;
        }
        String[] objs = obj.split(",");
        StringJoiner result = new StringJoiner(",");
        for (String value : objs) {
            Object val = SpringUtils.getBean("redisTemplate", RedisTemplate.class).opsForHash().get(key, value);
            if (!ObjectUtils.isEmpty(val)) {
                result.add(String.valueOf(val));
            } else {
                result.add("");
            }
        }
        if (PaasUtils.isEmpty(setField)) {
            jsonGenerator.writeString(result.toString());
        } else {
            jsonGenerator.writeString(obj);
            jsonGenerator.writeStringField(setField, result.toString());
        }
    }
}
