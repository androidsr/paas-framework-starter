package paas.framework.redis.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import paas.framework.redis.annotation.RedisLock;

import jakarta.annotation.Resource;
import java.util.StringJoiner;

/**
 * AOP实现Redis缓存处理
 */
@Slf4j
@Component
@Aspect
public class LockAspect {

    @Resource
    RedissonClient redissonClient;

    @Value("${paas.redis.prefix:true}")
    private Boolean prefix;

    @Pointcut("@annotation(paas.framework.redis.annotation.RedisLock)")
    public void pointcutMethod() {

    }

    @Around("pointcutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        RedisLock annotation = signature.getMethod().getAnnotation(RedisLock.class);
        ExpressionParser parser = new ExpressionParser();
        String key = annotation.key();
        StringJoiner join = new StringJoiner(":");
        if (prefix && annotation.prefix()) {
            join.add(className);
            join.add(methodName);
        }
        String[] expAll = key.split(":");
        for (String exp : expAll) {
            if (exp.startsWith("#header")) {
                join.add(CommonUtils.setHeaderType(exp));
            } else {
                join.add(parser.generateKeyBySpEL(exp, joinPoint));
            }
        }

        String lockKey = join.toString();
        log.debug("redisson 准备加锁 key: {}", lockKey);
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.tryLock(annotation.waitTime(), annotation.leaseTime(), annotation.timeUnit());
            return joinPoint.proceed();
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
            log.debug("redisson 解锁成功: {}", lockKey);
        }
    }
}
