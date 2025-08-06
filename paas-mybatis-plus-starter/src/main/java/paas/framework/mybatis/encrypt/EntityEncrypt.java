package paas.framework.mybatis.encrypt;

import java.lang.annotation.*;

/**
 * 字段加解密注解
 * 放到实体类上
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityEncrypt {
}
