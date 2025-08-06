package paas.framework.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import java.util.List;

/**
 * @author: sirui
 * @date: 2021/11/11 19:19
 */
@Data
@AutoConfiguration
@ConfigurationProperties(prefix = "paas.redis")
public class RedisConfigProperties {
    private List<String> headerKey;

}
