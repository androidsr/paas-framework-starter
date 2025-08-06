package paas.framework.web.formatter;

import org.springframework.format.Formatter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeFormatter implements Formatter<Date> {
    @Override
    public Date parse(String text, Locale locale) throws ParseException {
        if ("".equals(text)) {
            return null;
        }
        if (text.matches("^\\d{4}-\\d{1,2}$")) {
            return parseDate(text, "yyyy-MM");
        } else if (text.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return parseDate(text, "yyyy-MM-dd");
        } else if (text.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            return parseDate(text, "yyyy-MM-dd hh:mm");
        } else if (text.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(text, "yyyy-MM-dd hh:mm:ss");
        } else {
            return new Date(text);
        }
    }

    @Override
    public String print(Date object, Locale locale) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(object);
    }


    public Date parseDate(String dateStr, String format) {
        Date date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {

        }
        return date;
    }
}