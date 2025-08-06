package paas.framework.gateway.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

public class WebFluxUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 设置webflux模型响应
     *
     * @param response ServerHttpResponse
     * @param value    响应内容
     * @return Mono<Void>
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, Object value) {
        return webFluxResponseWriter(response, response.getStatusCode(), value);
    }

    /**
     * 设置webflux模型响应
     *
     * @param response ServerHttpResponse
     * @param status   http状态码
     * @param value    响应内容
     * @return Mono<Void>
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, HttpStatusCode status, Object value) {
        return webFluxResponseWriter(response, MediaType.APPLICATION_JSON_VALUE, status, value);
    }

    /**
     * 设置webflux模型响应
     *
     * @param response    ServerHttpResponse
     * @param contentType content-type
     * @param status      http状态码
     * @param value       响应内容
     * @return Mono<Void>
     */
    public static Mono<Void> webFluxResponseWriter(ServerHttpResponse response, String contentType, HttpStatusCode status, Object value) {
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, contentType);
        DataBuffer dataBuffer;
        try {
            dataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsString(value).getBytes());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return response.writeWith(Mono.just(dataBuffer));
    }
}
