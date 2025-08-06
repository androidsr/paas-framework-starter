package paas.framework.jpush;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jpush") // 从配置文件的前缀拿
public class JpushProperties {
    private String appKey;
    private String masterSecret;
    private Long liveTime;
    private Boolean apnsProduction;
}
