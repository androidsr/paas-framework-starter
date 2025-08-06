
package paas.framework.redis.annotation;


import paas.framework.redis.enums.CacheTypeEnum;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisCache {

    String key() default "";

    CacheTypeEnum type() default CacheTypeEnum.CREATE;

    long expireTime() default 1;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    boolean autoRefresh() default false;
    boolean prefix() default true;
}