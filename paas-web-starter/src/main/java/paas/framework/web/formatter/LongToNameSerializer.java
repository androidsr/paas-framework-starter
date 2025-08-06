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

public class LongToNameSerializer extends JsonSerializer<Long> implements ContextualSerializer {

    private String key;
    private String setField;

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), Long.class)) {
                LongToName annotation = beanProperty.getAnnotation((LongToName.class));
                if (annotation == null) {
                    annotation = beanProperty.getContextAnnotation(LongToName.class);
                }
                LongToNameSerializer serializer = new LongToNameSerializer();
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
    public void serialize(Long obj, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (obj == null) {
            return;
        }
        String value;
        Object val = SpringUtils.getBean("redisTemplate", RedisTemplate.class).opsForHash().get(key, String.valueOf(obj));
        if (!ObjectUtils.isEmpty(val)) {
            value = String.valueOf(val);
        } else {
            value = "";
        }
        if (PaasUtils.isEmpty(setField)) {
            jsonGenerator.writeString(value);
        } else {
            jsonGenerator.writeNumber(obj);
            jsonGenerator.writeStringField(setField, value);
        }
    }
}
