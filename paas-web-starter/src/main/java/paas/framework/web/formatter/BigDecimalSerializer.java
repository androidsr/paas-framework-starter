package paas.framework.web.formatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

//@JsonComponent
public class BigDecimalSerializer extends JsonSerializer<BigDecimal> implements ContextualSerializer {
    private String format = "0.00";
    private BigDecimalFormat.Unit unit = BigDecimalFormat.Unit.Y;

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), BigDecimal.class)) {
                BigDecimalFormat bigDecimalFormat = beanProperty.getAnnotation((BigDecimalFormat.class));
                if (bigDecimalFormat == null) {
                    bigDecimalFormat = beanProperty.getContextAnnotation(BigDecimalFormat.class);
                }
                BigDecimalSerializer bigDecimalSerializer = new BigDecimalSerializer();
                if (bigDecimalFormat != null) {
                    bigDecimalSerializer.format = bigDecimalFormat.value();
                    bigDecimalSerializer.unit = bigDecimalFormat.unit();
                }
                return bigDecimalSerializer;
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }

    @Override
    public void serialize(BigDecimal bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (bigDecimal == null) {
            return;
        }
        if (bigDecimal.doubleValue() == 0) {
            jsonGenerator.writeNumber(0);
            return;
        }
        if (unit == BigDecimalFormat.Unit.J) {
            bigDecimal = bigDecimal.divide(new BigDecimal("10"), 4, RoundingMode.DOWN);
        } else if (unit == BigDecimalFormat.Unit.F) {
            bigDecimal = bigDecimal.divide(new BigDecimal("100"), 4, RoundingMode.DOWN);
        }

        DecimalFormat decimalFormat = new DecimalFormat(format);
        decimalFormat.setRoundingMode(RoundingMode.DOWN);
        if (format.contains(",")) {
            jsonGenerator.writeString(decimalFormat.format(bigDecimal));
        } else {
            jsonGenerator.writeNumber(decimalFormat.format(bigDecimal));
        }
    }
}