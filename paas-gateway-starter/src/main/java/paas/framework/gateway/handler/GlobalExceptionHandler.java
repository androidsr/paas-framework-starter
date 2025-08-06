package paas.framework.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import paas.framework.gateway.utils.WebFluxUtils;
import paas.framework.model.exception.BusException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 网关统一异常处理
 *
 * @author sirui
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@AutoConfiguration
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        Map<String, Object> error = new HashMap<>();
        if (ex instanceof NotFoundException) {
            error.put("code", 404);
            error.put("msg", "服务未找到");
        } else if (ex instanceof ResponseStatusException) {
            ResponseStatusException responseStatusException = (ResponseStatusException) ex;
            error.put("code", responseStatusException.getStatusCode());
            error.put("msg", responseStatusException.getMessage());
        } else if (ex instanceof BusException) {
            BusException exception = (BusException) ex;
            if (exception.getCode() == 401) {
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
            }
            error.put("code", exception.getCode());
            error.put("msg", exception.getMessage());
        } /*else if (ex instanceof BlockException) {
            error.put("code", 429);
            error.put("msg", "服务器接收请求过多，已限流处理");
        } */ else if (ex instanceof WebClientResponseException) {
            HttpStatusCode httpStatus = ((WebClientResponseException) ex).getStatusCode();
            if (httpStatus != null) {
                error.put("code", httpStatus.value());
                error.put("msg", httpStatus.isError());
            }
        } else {
            error.put("code", 500);
            error.put("msg", "网关错误");
        }
        log.error("网关全局异常处理：{}", ex.getMessage());
        //ex.printStackTrace();
        return WebFluxUtils.webFluxResponseWriter(response, error);
    }
}