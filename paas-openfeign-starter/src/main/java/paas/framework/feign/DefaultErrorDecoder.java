package paas.framework.feign;

import feign.FeignException;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import paas.framework.model.enums.ResultMessage;
import paas.framework.model.exception.BusException;

@Slf4j
//@AutoConfiguration
public class DefaultErrorDecoder extends ErrorDecoder.Default {

    @Override
    public Exception decode(String methodKey, Response response) {
        Exception exception = super.decode(methodKey, response);
        if (exception instanceof RetryableException) {
            return exception;
        }
        log.error("Feign Global Error:{}", exception.getMessage());
        if (exception instanceof FeignException && ((FeignException) exception).responseBody().isPresent()) {
            return new BusException(ResultMessage.FEIGN_FAILURE);
        }
        return exception;
    }
}