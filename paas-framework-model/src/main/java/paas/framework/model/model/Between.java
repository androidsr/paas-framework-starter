package paas.framework.model.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
public class Between implements Serializable {
    private Date start;
    private Date end;

    public void setStart(Object start) {
        this.start = strToDate(start);
    }

    public void setEnd(Object end) {
        this.end = strToDate(end);
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    private static Date strToDate(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        }
        if (Objects.isNull(value)) {
            return null;
        }
        if (value instanceof String) {
            if (value.toString().matches("^\\d{4}-\\d{1,2}$")) {
                return parseDate(value.toString(), "yyyy-MM");
            } else if (value.toString().matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
                return parseDate(value.toString(), "yyyy-MM-dd");
            } else if (value.toString().matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
                return parseDate(value.toString(), "yyyy-MM-dd hh:mm");
            } else if (value.toString().matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
                return parseDate(value.toString(), "yyyy-MM-dd hh:mm:ss");
            }
        } else if (value instanceof Long) {
            return new Date(Long.parseLong(value.toString()));
        }
        return null;
    }

    public static Date parseDate(String dateStr, String format) {
        Date date = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat(format);
            date = dateFormat.parse(dateStr);
        } catch (Exception e) {

        }
        return date;
    }
}