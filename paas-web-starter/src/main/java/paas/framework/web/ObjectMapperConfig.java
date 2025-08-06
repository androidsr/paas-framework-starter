package paas.framework.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import paas.framework.web.formatter.DateDeserializer;
import paas.framework.web.formatter.DateSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class ObjectMapperConfig {
    /**
     * 默认日期时间格式
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 默认日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 默认时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    private static ObjectMapper configGlobal(ObjectMapper objectMapper) {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        // 设置格式化内容
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        // 忽略无法转换的对象
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // 指定时区
        objectMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        return objectMapper;
    }

    public static ObjectMapper config(ObjectMapper mapper) {
        ObjectMapper objectMapper = configGlobal(mapper);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addDeserializer(Date.class, new DateDeserializer());
        //simpleModule.addSerializer(Date.class, new DateSerializer());
        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(java.time.format.DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(java.time.format.DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer(java.time.format.DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(java.time.format.DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(java.time.format.DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        simpleModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));


        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }

    public static ObjectMapper configFeign(ObjectMapper mapper) {
        ObjectMapper objectMapper = configGlobal(mapper);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // java8日期日期处理
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        simpleModule.addSerializer(Date.class, new DateSerializer());
        simpleModule.addDeserializer(Date.class, new DateDeserializer());

        simpleModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(java.time.format.DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        simpleModule.addSerializer(LocalDate.class, new LocalDateSerializer(java.time.format.DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        simpleModule.addSerializer(LocalTime.class, new LocalTimeSerializer(java.time.format.DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
        simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(java.time.format.DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)));
        simpleModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(java.time.format.DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)));
        simpleModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));

        objectMapper.registerModule(simpleModule);
        return objectMapper;
    }
}
