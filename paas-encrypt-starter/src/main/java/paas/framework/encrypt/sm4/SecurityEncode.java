package paas.framework.encrypt.sm4;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JacksonAnnotation
@JsonDeserialize(using = SecurityEncodeDeserializer.class)
public @interface SecurityEncode {
    String desensitize() default "*";

    SecurityType securityType() default SecurityType.NONE;

    @Getter
    @AllArgsConstructor
    enum SecurityType {
        NONE("无"),
        SM4("国密4"),
        ;
        private String desc;

    }
}
