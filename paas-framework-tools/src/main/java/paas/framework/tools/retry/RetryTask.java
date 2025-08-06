package paas.framework.tools.retry;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class RetryTask implements Runnable {
    private Runnable task;
    private RetryCondition retryCondition;
    private int maxRetries;
    private long initialDelayMillis;
    private long maxDelayMillis;
    private long delayIncrementMillis;
    private ScheduledExecutorService executor;

    public RetryTask(Runnable task, RetryCondition retryCondition, int maxRetries, long initialDelayMillis, long maxDelayMillis, long delayIncrementMillis) {
        this.task = task;
        this.retryCondition = retryCondition;
        this.maxRetries = maxRetries;
        this.initialDelayMillis = initialDelayMillis;
        this.maxDelayMillis = maxDelayMillis;
        this.delayIncrementMillis = delayIncrementMillis;
        this.executor = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void run() {
        if (retryCondition.shouldRetry() && maxRetries > 0) {
            try {
                task.run();
            } catch (Exception e) {
                maxRetries--;
                long delay = initialDelayMillis + (delayIncrementMillis * (maxRetries - 1));
                delay = Math.min(delay, maxDelayMillis);
                executor.schedule(this, delay, TimeUnit.SECONDS);
            }
        } else {
            executor.shutdown();
        }
    }
}
