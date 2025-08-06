package paas.framework.web.formatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DateDeserializer extends JsonDeserializer<Date> {

    private static final Logger LOGGER = Logger.getLogger(DateDeserializer.class.getName());

    private static final String[] DATE_FORMATS = {
            "yyyy-MM-dd",               // 年-月-日
            "yyyy-MM-dd HH:mm",         // 年-月-日 时:分
            "yyyy-MM-dd HH:mm:ss",      // 年-月-日 时:分:秒
            "yyyy/MM/dd",               // 年/月/日
            "yyyyMMdd",                 // 年月日
            "yyyyMMddHHmm",             // 年月日时分
            "yyyyMMddHHmmss"            // 年月日时分秒
    };

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        // 获取当前 token
        JsonToken token = jsonParser.getCurrentToken();

        // 如果是字符串类型（日期字符串）
        if (token == JsonToken.VALUE_STRING) {
            String value = jsonParser.getValueAsString();
            // 如果是空字符串，返回 null
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            // 判断是否为时间戳格式
            if (isNumeric(value)) {
                try {
                    return new Date(Long.parseLong(value));  // 解析为时间戳
                } catch (NumberFormatException e) {
                    LOGGER.log(Level.WARNING, "无法解析时间戳: " + value, e);
                }
            }
            // 否则，尝试解析为日期
            for (String format : DATE_FORMATS) {
                Date date = parseDate(value, format);
                if (date != null) {
                    return date;  // 成功解析
                }
            }
            // 如果无法解析，记录警告并返回 null
            LOGGER.log(Level.WARNING, "无法解析日期字符串: " + value);
        }
        // 如果是日期类型（直接返回 Date）
        else if (token == JsonToken.VALUE_NUMBER_INT || token == JsonToken.VALUE_NUMBER_FLOAT) {
            // 可能是时间戳，直接返回
            long timestamp = jsonParser.getLongValue();
            return new Date(timestamp);
        }
        // 如果不是预期的类型，返回 null
        return null;
    }

    // 判断字符串是否为数字（时间戳）
    private boolean isNumeric(String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 尝试使用给定格式解析日期
    private Date parseDate(String dateStr, String format) {
        Date date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            LOGGER.log(Level.FINE, "日期解析失败，格式: " + format + "，值: " + dateStr, e);
        }
        return date;
    }
}
