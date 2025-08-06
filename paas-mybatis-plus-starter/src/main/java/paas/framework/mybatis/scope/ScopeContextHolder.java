package paas.framework.mybatis.scope;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Objects;

public class ScopeContextHolder {
    private static final TransmittableThreadLocal<ScopeInfo> CONTEXT = new TransmittableThreadLocal<>();

    public static ScopeInfo getContext() {
        if (Objects.isNull(CONTEXT.get())) {
            synchronized (ScopeContextHolder.class) {
                if (Objects.isNull(CONTEXT.get())) {
                    CONTEXT.set(ScopeInfo.create());
                }
            }
        }
        return CONTEXT.get();
    }

    public static void remove() {
        CONTEXT.remove();
    }
}