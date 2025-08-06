package paas.framework.redis.aspect;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import paas.framework.model.enums.ResultMessage;
import paas.framework.model.exception.BusException;
import paas.framework.model.web.NetResult;
import paas.framework.redis.annotation.RedisCache;
import paas.framework.redis.enums.CacheTypeEnum;
import paas.framework.tools.PaasUtils;

import java.util.Collection;
import java.util.StringJoiner;

/**
 * AOP实现Redis缓存处理
 */
@Slf4j
@Component
@Aspect
@RefreshScope
public class CacheAspect {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Value("${paas.redis.enable:true}")
    private Boolean enable;

    @Value("${paas.redis.prefix:true}")
    private Boolean prefix;

    @Pointcut("@annotation(paas.framework.redis.annotation.RedisCache)")
    public void pointcutMethod() {

    }

    @Around("pointcutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        if (!enable) {
            return joinPoint.proceed();
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getName();
        RedisCache annotation = signature.getMethod().getAnnotation(RedisCache.class);
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

        String keyValue = join.toString();
        if (PaasUtils.isEmpty(keyValue)) {
            throw new BusException(ResultMessage.CACHE_ERROR);
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (request != null) {
                Object refresh = request.getHeader("redisRefresh");
                if (!ObjectUtils.isEmpty(refresh) && refresh.equals("true")) {
                    redisTemplate.delete(keyValue);
                }
            }
        }

        if (annotation.type() == CacheTypeEnum.DELETE) {
            try {
                redisTemplate.delete(keyValue);
            } catch (Exception e) {
                log.error("Redis缓存，异常：{}", keyValue);
                e.printStackTrace();
            }
            return joinPoint.proceed();
        }
        Object ret = null;
        try {
            ret = redisTemplate.opsForValue().get(keyValue);
        } catch (Exception e) {
            log.error("Redis缓存，异常：{}", keyValue);
            e.printStackTrace();
        }
        if (ret != null) {
            if (annotation.autoRefresh()) {
                redisTemplate.expire(keyValue, annotation.expireTime(), annotation.timeUnit());
            }
            if (checkResult(ret)) {
                return ret;
            }
        }
        ret = joinPoint.proceed();
        try {
            if (!checkResult(ret)) {
                return ret;
            }

            redisTemplate.opsForValue().set(keyValue, ret, annotation.expireTime(), annotation.timeUnit());
        } catch (Exception e) {
            log.error("Redis缓存，异常：{}", keyValue);
            e.printStackTrace();
        }
        return ret;
    }

    private Boolean checkResult(Object ret) {
        switch (ret) {
            case null -> {
                return false;
            }
            case Collection collection -> {
                return PaasUtils.isNotEmpty(collection);
            }
            case NetResult result -> {
                if (result.getCode() != ResultMessage.SUCCESS.getCode()) {
                    return false;
                }
                if (result.getData() == null) {
                    return false;
                }
                if (result.getData() instanceof Collection collection) {
                    return PaasUtils.isNotEmpty(collection);
                }
            }
            default -> {
            }
        }
        return true;
    }
}
