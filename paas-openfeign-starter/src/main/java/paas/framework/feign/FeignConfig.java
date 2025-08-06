package paas.framework.feign;

import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@AutoConfiguration
public class FeignConfig {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new DefaultFeignInterceptor();
    }

    /*@Bean
    public ErrorDecoder errorDecoder() {
        return new DefaultErrorDecoder();  // 使用自定义的 ErrorDecoder
    }
*/
    @Bean
    public SpringEncoder springEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringEncoder(messageConverters);
    }
}
