package paas.framework.web;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * spring 工具类
 *
 * @author sirui
 */
@Component
@Lazy(value = false)
public class SpringUtils implements ApplicationContextAware {
    private static volatile ApplicationContext context;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        if (context == null) {
            synchronized (SpringUtils.class) {
                if (context == null) {
                    context = applicationContext;
                }
            }
        }
    }

    public static ApplicationContext getContext() {
        if (context == null) {
            synchronized (SpringUtils.class) {
                // 再次检查，确保线程安全
                if (context == null) {
                    throw new IllegalStateException("ApplicationContext is not initialized yet.");
                }
            }
        }
        return context;
    }

    // 获取Bean的同时检查其是否为懒加载
    public static <T> T getBean(Class<T> cls) {
        try {
            return getContext().getBean(cls);
        } catch (Exception e) {
            return initializeLazyBean(cls);
        }
    }

    // 根据 bean 名称获取并检查是否为懒加载
    public static <T> T getBean(String beanName, Class<T> cls) {
        try {
            return getContext().getBean(beanName, cls);
        } catch (Exception e) {
            return initializeLazyBean(beanName, cls);
        }
    }

    // 手动触发懒加载Bean的初始化（通过ApplicationContext）
    private static <T> T initializeLazyBean(Class<T> cls) {
        return getContext().getAutowireCapableBeanFactory().getBean(cls);
    }

    private static <T> T initializeLazyBean(String beanName, Class<T> cls) {
        return getContext().getAutowireCapableBeanFactory().getBean(beanName, cls);
    }
}