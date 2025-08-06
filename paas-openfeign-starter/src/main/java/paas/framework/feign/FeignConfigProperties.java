package paas.framework.feign;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author: sirui
 * @date: 2021/11/11 19:19
 */
@Data
@Component
@ConfigurationProperties(prefix = "paas.feign")
public class FeignConfigProperties {
    private String[] headerKey;
    private String[] cookieKey;

}
