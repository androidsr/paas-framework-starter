package paas.framework.web.formatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import paas.framework.tools.PaasUtils;

import java.io.IOException;
import java.util.Objects;

public class ConvertEnumIntegerSerializer extends JsonSerializer<Integer> implements ContextualSerializer {

    private Class<? extends EnumCode<?>> value;

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), Integer.class)) {
                ConvertEnum annotation = beanProperty.getAnnotation(ConvertEnum.class);
                if (annotation == null) {
                    annotation = beanProperty.getContextAnnotation(ConvertEnum.class);
                }
                ConvertEnumIntegerSerializer convertEnumIntegerSerializer = new ConvertEnumIntegerSerializer();
                if (annotation != null) {
                    convertEnumIntegerSerializer.value = annotation.value();
                }
                return convertEnumIntegerSerializer;
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }

    @Override
    public void serialize(Integer value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        for (EnumCode item : this.value.getEnumConstants()) {
            if (PaasUtils.equals(item.getCode().toString(), String.valueOf(value))) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName("value");
                jsonGenerator.writeNumber(value);
                jsonGenerator.writeFieldName("name");
                jsonGenerator.writeString(item.getTitle());
                jsonGenerator.writeEndObject();
                return;
            }
        }
    }
}