package paas.framework.gateway.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@AutoConfiguration
public class WebClientConfig {
    /**
     * 微服务调用专用客户端：支持负载均衡 + 连接池
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder cloudWebClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("pooled-cloud-client").maxConnections(100).pendingAcquireMaxCount(-1).build();
        HttpClient httpClient = HttpClient.create(connectionProvider).keepAlive(true).compress(true);
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    /**
     * 普通 HTTP 调用客户端：不走服务发现，适用于访问外部 URL
     */
    @Bean
    public WebClient.Builder httpWebClient() {
        // 创建连接池
        ConnectionProvider connectionProvider = ConnectionProvider.builder("pooled-http-client").maxConnections(50).pendingAcquireMaxCount(-1).build();
        HttpClient httpClient = HttpClient.create(connectionProvider).keepAlive(true).compress(true);
        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));
    }
}