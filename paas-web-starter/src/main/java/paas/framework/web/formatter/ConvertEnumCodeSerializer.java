package paas.framework.web.formatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class ConvertEnumCodeSerializer extends JsonSerializer<EnumCode> {

    @Override
    public void serialize(EnumCode value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("value");
        jsonGenerator.writeObject(value.getCode());
        jsonGenerator.writeFieldName("name");
        jsonGenerator.writeString(value.getTitle());
        jsonGenerator.writeEndObject();
    }
}