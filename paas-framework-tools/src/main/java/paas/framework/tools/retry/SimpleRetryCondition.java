package paas.framework.tools.retry;

class SimpleRetryCondition implements RetryCondition {
    private boolean condition;

    public SimpleRetryCondition(boolean condition) {
        this.condition = condition;
    }

    @Override
    public boolean shouldRetry() {
        return condition;
    }
}