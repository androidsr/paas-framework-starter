package paas.framework.xxl;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@Slf4j
@AutoConfiguration
public class XxlJobConfig {
    @Value("${xxl.job.admin.addresses}")
    String adminAddresses;
    @Value("${xxl.job.accessToken}")
    String accessToken;
    @Value("${xxl.job.executor.appname}")
    String appName;
    @Value("${xxl.job.executor.address}")
    String address;
    @Value("${xxl.job.executor.ip}")
    String ip;
    @Value("${xxl.job.executor.port}")
    int port;
    @Value("${xxl.job.executor.logpath}")
    String logPath;
    @Value("${xxl.job.executor.logretentiondays}")
    int logRetentionDays;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor() {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setAddress(address);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        return xxlJobSpringExecutor;
    }
}