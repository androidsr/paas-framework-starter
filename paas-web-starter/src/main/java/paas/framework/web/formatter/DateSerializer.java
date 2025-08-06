package paas.framework.web.formatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException {
        String formattedDate;
        try {
            formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
            //formattedDate = formattedDate.replace(" 00:00:00", "");
        } catch (Exception e) {
            formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        }
        gen.writeString(formattedDate);
    }
}