package paas.framework.tools.retry;

interface RetryCondition {
    boolean shouldRetry();
}