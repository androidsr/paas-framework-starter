package paas.framework.redis.annotation;


import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {
    String key() default "";

    long waitTime() default 30;

    long leaseTime() default -1;

    TimeUnit timeUnit() default TimeUnit.SECONDS;

    boolean prefix() default true;
}