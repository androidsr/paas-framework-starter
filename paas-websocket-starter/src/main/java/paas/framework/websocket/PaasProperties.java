package paas.framework.websocket;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * @author: sirui
 * @date: 2021/11/11 19:19
 */
@Data
@AutoConfiguration
@ConfigurationProperties(prefix = "paas.websocket", ignoreInvalidFields = true)
public class PaasProperties {
    private Long beatIdleTime = 60L;
}
