package paas.framework.encrypt.desensitize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import paas.framework.encrypt.sm4.CypherSm4Utils;
import paas.framework.encrypt.sm4.SecurityEncode;

import java.io.IOException;
import java.util.Objects;

public class DesensitizeJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private DesensitizeRule rule;
    private SecurityEncode.SecurityType securityType;

    @Override
    public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (securityType != SecurityEncode.SecurityType.NONE) {
            switch (securityType) {
                case SM4: {
                    try {
                        value = CypherSm4Utils.decode(value);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        gen.writeString(rule.desensitize().apply(value));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        Desensitize annotation = property.getAnnotation(Desensitize.class);
        if (Objects.nonNull(annotation) && Objects.equals(String.class, property.getType().getRawClass())) {
            this.rule = annotation.rule();
            this.securityType = annotation.securityType();
            return this;
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}