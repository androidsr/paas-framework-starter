package paas.framework.mybatis.dynamic;

import com.alibaba.ttl.TransmittableThreadLocal;


public class DynamicTableConfig {
    private static final TransmittableThreadLocal<String> threadLocal = new TransmittableThreadLocal<>();

    public static String get() {
        return threadLocal.get();
    }

    public static void set(String requestData) {
        threadLocal.set(requestData);
    }

    public static void remove() {
        try {
            threadLocal.remove();
        } catch (Exception e) {

        }
    }
}