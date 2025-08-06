package paas.framework.mybatis.encrypt;

import java.lang.annotation.*;

/**
 * 字段加解密注解
 * 放到需要加解密的字段上
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldEncrypt {
}
