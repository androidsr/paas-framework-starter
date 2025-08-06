package paas.framework.redis;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import paas.framework.model.enums.ResultMessage;
import paas.framework.model.exception.BusException;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class LockHelper {

    @Resource
    RedissonClient redissonClient;

    /**
     * 分布式自动续约锁
     *
     * @param key      锁定key
     * @param waitTime 尝试加锁等待时间（秒）
     * @return
     */
    public RLock lock(String key, long waitTime) {
        RLock lock = redissonClient.getLock(key);
        try {
            lock.tryLock(waitTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return lock;
    }

    /**
     * 分布式不续约锁
     *
     * @param key       锁定key
     * @param waitTime  尝试加锁等待时间（秒）
     * @param leaseTime 锁过期时间（秒）
     * @return
     */
    public RLock lock(String key, long waitTime, long leaseTime) {
        RLock lock = redissonClient.getLock(key);
        if (waitTime > leaseTime) {
            log.warn("分布式锁使用警告：{}", "最大等待时间大于过期时间不建议这样使用...");
        }
        try {
            lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
        return lock;
    }

    public <T> T lock(String key, long waitTime, SuccessCallback callback) {
        return lock(key, waitTime, callback, null);
    }

    /**
     * 分布式自动续约锁
     *
     * @param key      锁定key
     * @param waitTime 尝试加锁等待时间（秒）
     * @param callback 业务处理函数(返回结果)
     */
    public <T> T lock(String key, long waitTime, SuccessCallback callback, FailCallback fail) {
        RLock lock = redissonClient.getLock(key);
        try {
            boolean lockFlag = lock.tryLock(waitTime, TimeUnit.SECONDS);
            if (lockFlag) {
                return (T) callback.success();
            } else {
                log.warn("分布式锁失败-{}：{}", key, "获取分布式锁失败...");
                if (fail != null) {
                    fail.error();
                }
            }
        } catch (BusException bus) {
            bus.printStackTrace();
            throw bus;
        } catch (Exception e) {
            e.printStackTrace();
            BusException.fail(ResultMessage.LOCK_ERROR);
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return null;
    }

    public <T> T lock(String key, long waitTime, long leaseTime, SuccessCallback callback) {
        return lock(key, waitTime, leaseTime, callback, null);
    }

    /**
     * 分布式不续约锁
     *
     * @param key       锁定key
     * @param leaseTime 锁过期时间（秒）
     * @param waitTime  尝试加锁等待时间（秒）
     * @param callback  成功业务处理函数
     * @param fail      失败业务处理函数
     */
    public <T> T lock(String key, long waitTime, long leaseTime, SuccessCallback callback, FailCallback fail) {
        RLock lock = redissonClient.getLock(key);
        if (waitTime > leaseTime) {
            log.warn("分布式锁使用警告：{}", "最大等待时间大于过期时间不建议这样使用...");
        }
        try {
            boolean lockFlag = lock.tryLock(waitTime, leaseTime, TimeUnit.SECONDS);
            if (lockFlag) {
                return (T) callback.success();
            } else {
                log.warn("分布式锁失败-{}：{}", key, "获取分布式锁失败...");
                if (fail != null) {
                    fail.error();
                }
            }
        } catch (BusException bus) {
            bus.printStackTrace();
            throw bus;
        } catch (Exception e) {
            e.printStackTrace();
            BusException.fail(ResultMessage.LOCK_ERROR);
        } finally {
            if (lock != null && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return null;
    }

    /**
     * 分布式解锁
     */
    public void unlock(RLock lock) {
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    public interface SuccessCallback {
        Object success();
    }

    public interface FailCallback {
        void error();
    }
}
