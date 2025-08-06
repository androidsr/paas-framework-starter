package paas.framework.encrypt.sm4;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import paas.framework.tools.PaasUtils;

import java.io.IOException;
import java.util.Objects;

public class SecurityEncodeDeserializer extends JsonDeserializer<String> implements ContextualDeserializer {

    private String desensitize;
    private SecurityEncode.SecurityType securityType;

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String value = jsonParser.getValueAsString();
        if (PaasUtils.isEmpty(value)) {
            return value;
        }
        if (PaasUtils.isNotEmpty(desensitize) && value.contains(desensitize)) {
            return null;
        }
        if (securityType != SecurityEncode.SecurityType.NONE) {
            switch (securityType) {
                case SM4: {
                    try {
                        //进行解密处理确认是否已经是加密数据；解密成功对值不做处理。
                        CypherSm4Utils.decode(value);
                    } catch (Exception e) {
                        //解密失败进行加密处理
                        value = CypherSm4Utils.encode(value);
                    }
                    break;
                }
            }
        }
        return value;
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext prov, BeanProperty property) throws JsonMappingException {
        SecurityEncode annotation = property.getAnnotation(SecurityEncode.class);
        if (Objects.nonNull(annotation) && Objects.equals(String.class, property.getType().getRawClass())) {
            this.desensitize = annotation.desensitize();
            this.securityType = annotation.securityType();
            return this;
        }
        return prov.findContextualValueDeserializer(property.getType(), property);
    }
}