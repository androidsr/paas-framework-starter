package paas.framework.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
public class MinioConfig {

    @Autowired
    MinioProperties minioProperties;

    @Bean
    public MinioClient minioClient() {
        if (minioProperties.getPort() != 0) {
            return MinioClient.builder().endpoint(minioProperties.getEndpoint(), minioProperties.getPort(), minioProperties.isSecure()).credentials(minioProperties.getMinioRootUser(), minioProperties.getMinioRootPassword()).build();
        } else {
            return MinioClient.builder().endpoint(minioProperties.getEndpoint()).credentials(minioProperties.getMinioRootUser(), minioProperties.getMinioRootPassword()).build();
        }
    }

}
