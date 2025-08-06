package paas.framework.encrypt.desensitize;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import paas.framework.encrypt.sm4.SecurityEncode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 脱敏处理
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = DesensitizeJsonSerializer.class)
public @interface Desensitize {
    DesensitizeRule rule();

    SecurityEncode.SecurityType securityType() default SecurityEncode.SecurityType.NONE;
}