package paas.framework.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import java.util.List;

/**
 * 放行白名单配置
 */
@Data
@AutoConfiguration
@RefreshScope
@ConfigurationProperties(prefix = "security.ignore")
public class IgnoreWhiteProperties {
    private List<String> whites;
}