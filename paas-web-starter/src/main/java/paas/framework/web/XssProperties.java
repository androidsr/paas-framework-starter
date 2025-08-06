package paas.framework.web;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "paas.xss-filter") // 从配置文件的前缀拿
public class XssProperties {
    private List<String> excludes;
}
