package paas.framework.web.formatter;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = BigDecimalSerializer.class)
public @interface BigDecimalFormat {
    String value() default "0.00";

    Unit unit() default Unit.Y;

    enum Unit {
        Y,
        J,
        F;

        Unit() {
        }
    }
}