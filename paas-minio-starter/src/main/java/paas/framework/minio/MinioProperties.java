package paas.framework.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "minio") // 从配置文件的前缀拿
public class MinioProperties {
    private String endpoint;
    private Integer port;
    private boolean secure;
    private String minioRootUser;
    private String minioRootPassword;
}
