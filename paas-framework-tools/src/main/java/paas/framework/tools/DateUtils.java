package paas.framework.tools;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    /**
     * 获取当前日期
     *
     * @return （年月日 00:00:00）
     */
    public static Date getDate() {
        return toDate(LocalDate.now());
    }

    /**
     * 获取当前日期
     *
     * @return （年月日 时分秒）
     */
    public static Date getDateTime() {
        return toDateTime(LocalDateTime.now());
    }

    /**
     * 获取当前日期字符串
     *
     * @param pattern 日期格式
     * @return 字符串：yyyy-MM-dd 或 yyyyMMdd
     */
    public static String getDate(DateEnum pattern) {
        return LocalDate.now().format(pattern.getFormatter());
    }

    /**
     * 获取当前日期字符串
     *
     * @return 字符串：yyyy-MM-dd 或 yyyyMMdd
     */
    public static Date getDate(Date date) {
        return toDate(toLocalDate(date));
    }

    /**
     * 获取当前日期字符串
     *
     * @param pattern 日期格式
     * @return 字符串：yyyy-MM-dd HH:mm:ss  或 yyyyMMddHHmmss
     */
    public static String getDateTime(DateTimeEnum pattern) {
        return LocalDateTime.now().format(pattern.getFormatter());
    }

    /**
     * 获取当前日期字符串
     */
    public static Date getDateTime(Date date) {
        return toDateTime(toLocalDateTime(date));
    }

    /**
     * 指定日期格式化字符串
     *
     * @param date    指定日期
     * @param pattern 日期格式
     * @return 字符串：yyyy-MM-dd 或 yyyyMMdd
     */
    public static String formatDate(LocalDate date, DateEnum pattern) {
        if (date == null){
            return "";
        }
        return date.format(pattern.getFormatter());
    }

    /**
     * 指定日期时间格式化字符串
     *
     * @param dateTime 指定日期时间
     * @param pattern  日期格式
     * @return 字符串：yyyy-MM-dd HH:mm:ss 或 yyyyMMddHHmmss
     */
    public static String formatDateTime(LocalDateTime dateTime, DateTimeEnum pattern) {
        if (dateTime == null){
            return "";
        }
        return dateTime.format(pattern.getFormatter());
    }

    /**
     * 指定日期时间格式化字符串
     *
     * @param dateTime 指定日期时间
     * @param pattern  日期格式
     * @return 字符串：yyyy-MM-dd HH:mm:ss 或 yyyyMMddHHmmss
     */
    public static String formatDateTime(Date dateTime, DateTimeEnum pattern) {
        if (dateTime == null){
            return "";
        }
        return toLocalDateTime(dateTime).format(pattern.getFormatter());
    }

    /**
     * 指定日期时间格式化字符串
     *
     * @param date    指定日期时间
     * @param pattern 日期格式
     * @return 字符串：yyyy-MM-dd 或 yyyyMMdd
     */
    public static String formatDate(Date date, DateEnum pattern) {
        if (date == null){
            return "";
        }
        return toLocalDate(date).format(pattern.getFormatter());
    }

    /**
     * 字符串格式化为日期
     *
     * @param text    字符串
     * @param pattern 字符串格式： yyyy-MM-dd 或 yyyyMMdd
     * @return （年月日 00:00:00）
     */
    public static Date parseDate(CharSequence text, DateEnum pattern) {
        return toDate(LocalDate.parse(text, pattern.getFormatter()));
    }

    /**
     * 字符串格式化为日期时间
     *
     * @param text    字符串
     * @param pattern 字符串格式：yyyy-MM-dd HH:mm:ss 或 yyyyMMddHHmmss
     * @return（年月日 时分秒）
     */
    public static Date parseDateTime(CharSequence text, DateTimeEnum pattern) {
        return toDateTime(LocalDateTime.parse(text, pattern.getFormatter()));
    }

    /**
     * LocalDate 转换 Date
     *
     * @param localDate 日期
     * @return
     */
    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }


    /**
     * LocalDateTime 转换 Date
     *
     * @param localDateTime 日期
     * @return
     */
    public static Date toDateTime(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    /**
     * Date 转换 LocalDate
     *
     * @param date 日期
     * @return
     */
    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }


    /**
     * Date 转换 LocalDateTime
     *
     * @param date 日期
     * @return
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * LocalDate 转换 LocalDateTime
     *
     * @param localDate
     * @return
     */
    public static LocalDateTime dateToDateTime(LocalDate localDate) {
        return localDate.atStartOfDay();
    }

    /**
     * LocalDateTime 转换 LocalDate
     *
     * @param localDateTime
     * @return
     */
    public static LocalDate dateTimeToDate(LocalDateTime localDateTime) {
        return localDateTime.toLocalDate();
    }

    /**
     * 日期加N天
     *
     * @param date 日期
     * @param days 增加天数
     * @return
     */
    public static LocalDate dateAddDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    /**
     * 日期减N天
     *
     * @param date 日期
     * @param days 增加天数
     * @return
     */
    public static LocalDate dateMinusDays(LocalDate date, int days) {
        return date.minusDays(days);
    }


    /**
     * 日期增运算
     *
     * @param dateTime 日期时间
     * @param size     加多少
     * @param unit     单位
     * @return
     */
    public static Date dateAdd(Date dateTime, int size, TimeUnit unit) {
        return toDateTime(dateAdd(toLocalDateTime(dateTime), size, unit));
    }

    /**
     * 日期增运算
     *
     * @param dateTime 日期时间
     * @param size     加多少
     * @param unit     单位
     * @return
     */
    public static LocalDateTime dateAdd(LocalDateTime dateTime, int size, TimeUnit unit) {
        switch (unit) {
            case DAYS:
                return dateTime.plusDays(size);
            case HOURS:
                return dateTime.plusHours(size);
            case MINUTES:
                return dateTime.plusMinutes(size);
            case SECONDS:
                return dateTime.plusSeconds(size);
        }
        return null;
    }

    /**
     * 日期减运算
     *
     * @param dateTime 日期时间
     * @param size     减多少
     * @param unit     单位
     * @return
     */
    public static Date dateMinus(Date dateTime, int size, TimeUnit unit) {
        return toDateTime(dateMinus(toLocalDateTime(dateTime), size, unit));
    }

    /**
     * 日期减运算
     *
     * @param dateTime 日期时间
     * @param size     减多少
     * @param unit     单位
     * @return
     */
    public static LocalDateTime dateMinus(LocalDateTime dateTime, int size, TimeUnit unit) {
        switch (unit) {
            case DAYS:
                return dateTime.minusDays(size);
            case HOURS:
                return dateTime.minusHours(size);
            case MINUTES:
                return dateTime.minusMinutes(size);
            case SECONDS:
                return dateTime.minusSeconds(size);
        }
        return null;
    }

    /**
     * 获取一天开始时间
     *
     * @param date
     * @return YYYY-MM-DD 00:00:00
     */
    public static Date getStartDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取一天结果时间
     *
     * @param date
     * @return YYYY-MM-DD 23:59:59
     */
    public static Date getEndDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 相差多少天
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int dayDiff(Date startDate, Date endDate) {
        long dif = DateUtil.between(startDate, endDate, DateUnit.DAY, false);
        return (int) dif;
    }

    /**
     * 相差
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static long dateDiff(Date startDate, Date endDate) {
        return endDate.getTime() - startDate.getTime();
    }
}