package paas.framework.feign;

import feign.RetryableException;
import feign.Retryer;
import org.springframework.context.annotation.Bean;

public class RetryConfig implements Retryer {
    @Override
    public void continueOrPropagate(RetryableException e) {
        throw e;
    }

    @Override
    public Retryer clone() {
        return new Default(3000, Integer.MAX_VALUE, 3);
    }
}
