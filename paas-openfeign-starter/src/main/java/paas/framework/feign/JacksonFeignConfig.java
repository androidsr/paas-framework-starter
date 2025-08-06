package paas.framework.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.codec.Decoder;
import feign.optionals.OptionalDecoder;
import jakarta.annotation.Resource;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import paas.framework.web.ObjectMapperConfig;

@Configuration
public class JacksonFeignConfig extends FeignClientsConfiguration {

    @Resource
    Jackson2ObjectMapperBuilder builder;

    /**
     * 默认解析器,
     * 将响应结果,下划线转驼峰
     */
    @Bean
    public Decoder customFeignDecoder() {
        return new OptionalDecoder(
                new ResponseEntityDecoder(new SpringDecoder(this::jacksonHttpMessageConverters)));
    }

    public HttpMessageConverters jacksonHttpMessageConverters() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = builder.build();
        converter.setObjectMapper(ObjectMapperConfig.configFeign(objectMapper));
        return new HttpMessageConverters(converter);
    }
}
