package paas.framework.rabbitmq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * @ClassName: RabbitmqProperties
 * @author: sirui
 * @date: 2021/11/11 19:19
 */
@Data
@AutoConfiguration
@ConfigurationProperties(prefix = "spring.rabbitmq")
public class RabbitmqProperties {
    private String host;
    private int port;
    private String username;
    private String password;
    private String virtualHost;
    private int connectionTimeout;
    private int publisherConfirmType;
    private Boolean publisherReturns;
    private Boolean mandatory;

}
