package paas.framework.web.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import paas.framework.web.ObjectMapperConfig;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@AutoConfiguration
public class JacksonConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = converter.getObjectMapper();
        converter.setObjectMapper(ObjectMapperConfig.config(objectMapper));
        List<MediaType> mediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        converter.setSupportedMediaTypes(mediaTypes);
        converters.add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        converters.add(1, converter);
    }
}