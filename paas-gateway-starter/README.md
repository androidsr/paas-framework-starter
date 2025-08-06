# gateway网关配置
## maven依赖
```xml
<parent>
    <artifactId>paas-framework-starter</artifactId>
    <groupId>paas.framework.starter</groupId>
    <version>2.0-SNAPSHOT</version>
    <relativePath/>
</parent>

<dependencies>
      <dependency>
          <groupId>paas.framework.starter</groupId>
          <artifactId>paas-sentinel-gateway-starter</artifactId>
      </dependency>
      <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-starter-gateway</artifactId>
      </dependency>
      <dependency>
          <groupId>paas.framework.starter</groupId>
          <artifactId>paas-zipkin-starter</artifactId>
      </dependency>
      <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-starter-bootstrap</artifactId>
      </dependency>
      <dependency>
          <groupId>com.alibaba.cloud</groupId>
          <artifactId>spring-cloud-starter-alibaba-nacos-config</artifactId>
      </dependency>
      <dependency>
          <groupId>com.alibaba.cloud</groupId>
          <artifactId>spring-cloud-starter-alibaba-nacos-discovery</artifactId>
      </dependency>
      <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-starter-openfeign</artifactId>
      </dependency>
      <dependency>
          <groupId>org.springframework.cloud</groupId>
          <artifactId>spring-cloud-loadbalancer</artifactId>
      </dependency>
      <dependency>
          <groupId>paas.framework.starter</groupId>
          <artifactId>paas-tools</artifactId>
      </dependency>
      <dependency>
          <groupId>paas.framework.starter</groupId>
          <artifactId>paas-redis-starter</artifactId>
          <exclusions>
              <exclusion>
                  <groupId>org.springframework</groupId>
                  <artifactId>spring-webmvc</artifactId>
              </exclusion>
          </exclusions>
      </dependency>
      <dependency>
          <groupId>paas.framework.starter</groupId>
          <artifactId>paas-kafka-starter</artifactId>
      </dependency>
      <!--<dependency>
          <groupId>paas.framework.starter</groupId>
          <artifactId>paas-sentinel-gateway-starter</artifactId>
      </dependency> -->
  </dependencies>
```
## yml配置
```yaml
spring:
###############链路跟踪###############
  zipkin:
    base-url: http://127.0.0.1:9411/
  cloud:
    loadbalancer:
      nacos:
        enabled: true
#########注册中心####################
    nacos:
      discovery:
        enabled: true
        server-addr: 127.0.0.1:8848
        namespace: "scm-dev"
        group: "DEFAULT_GROUP"
####################流控组件######################
    sentinel:
      #transport:
      #  dashboard: 127.0.0.1:8080
      #  eager: true
      filter:
        enabled: false
############局部过虑器配置##############
    gateway:
      discovery:
        locator:
          enabled: true
          filters:
          #  - AddRequestHeader=X-Request-Foo, Bar
            - SystemFilter=true
            - StripPrefix=1
###########网关全局配置项#####################
      globalcors:
        add-to-simple-url-handler-mapping: true
        cors-configurations:
          '[/**]':
            allowedOriginPatterns: 
              - "*"
            allowedMethods:
              - GET
              - POST
              - DELETE
              - PUT
              - OPTIONS
            allowedHeaders: "*"
            allowCredentials: true
            max-age: 360000
###############kafkamq消息队列###################
  kafka:
    bootstrap-servers: 127.0.0.1:9092
    producer:
      ##重试次数
      retries: 5
      ##批次大小
      batch-size: 16384
      ##缓冲区大小
      buffer-memory: 33554432
      acks: 1
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer
    consumer:
      group-id: default-group
      enable-auto-commit: true
      auto-commit-interval: 30000
      max-poll-records: 50
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      listener:
        type: batch 
        concurrency: 1 
################redis缓存配置#########################
  redis:
    database: 1
    timeout: 10000
    host: 127.0.0.1
    port: 6379
    password: 123456
    lettuce:
      pool:
        max-active: 500
        max-wait: -1
        max-idle: 10
        min-idle: 5

##########自定义认证白名单配置###################
security.ignore.whites:

###################监控开放配置################
management:
  endpoints:
    web:
      exposure:
        include: "*"
  endpoint:
    health:
      show-details: always
    shutdown:
      enabled: true

```
## 局部过虑器示例代码
```java

@Slf4j
@RefreshScope
@Component
public class SystemFilterGatewayFilterFactory extends AbstractGatewayFilterFactory<SystemFilterGatewayFilterFactory.Config> {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    IgnoreWhiteProperties ignoreWhiteProperties;
    @Autowired
    KafkaHelper kafkaHelper;


    public SystemFilterGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("enabled");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return new InnerFilter(config, redisTemplate, ignoreWhiteProperties, kafkaHelper);
    }

    public static class InnerFilter implements GatewayFilter, Ordered {
        private Config config;
        private RedisTemplate redisTemplate;
        private IgnoreWhiteProperties ignoreWhiteProperties;
        private KafkaHelper kafkaHelper;

        public InnerFilter(Config config, RedisTemplate redisTemplate, IgnoreWhiteProperties ignoreWhiteProperties, KafkaHelper kafkaHelper) {
            this.config = config;
            this.redisTemplate = redisTemplate;
            this.ignoreWhiteProperties = ignoreWhiteProperties;
            this.kafkaHelper = kafkaHelper;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            ServerHttpRequest request = exchange.getRequest();
            String uri = request.getURI().getPath();
            String token = request.getHeaders().getFirst(SystemConstants.AUTHORIZATION);
            String method = request.getMethod().name();

            uri = uri.substring(uri.indexOf("/", 1));
            if (this.config.enabled && uri.startsWith("/white/")) {
                SysLogRecordGatewayDTO dto = new SysLogRecordGatewayDTO();
                dto.setToken(token);
                dto.setMethod(method);
                dto.setUrl(uri);
                kafkaHelper.sendMessage(KafkaTopicConstants.SysLogRecord.TOPIC, JSONObject.toJSONString(dto));
                log.info("网关日志记录：{}", dto);
                return chain.filter(exchange);
            }

            for (String item : ignoreWhiteProperties.getWhites()) {
                if (uri.startsWith(item)) {
                    SysLogRecordGatewayDTO dto = new SysLogRecordGatewayDTO();
                    dto.setToken(token);
                    dto.setMethod(method);
                    dto.setUrl(uri);
                    kafkaHelper.sendMessage(KafkaTopicConstants.SysLogRecord.TOPIC, JSONObject.toJSONString(dto));
                    log.info("网关日志记录：{}", dto);
                    return chain.filter(exchange);
                }
            }


            if (PaasUtils.isEmpty(token)) {
                return fail(exchange, "认证失败");
            }
            JWT jwt = JWTUtil.parseToken(token);
            boolean verifyKey = jwt.setKey(LoginConstants.JWT_SECRET_KEY).verify();
            if (!verifyKey) {
                return fail(exchange, "登录已过期");
            }

            JWTPayload payload = jwt.getPayload();
            if (payload == null) {
                return fail(exchange, "登录已过期");
            }

            String userId = (String) payload.getClaim(LoginConstants.JWT_USER_ID);
            Integer expTime = (Integer) payload.getClaim(LoginConstants.JWT_EXP_TIME);
            Long expiresAt = (Long) payload.getClaim(JWTPayload.EXPIRES_AT);


            String key = String.join(":", LoginConstants.RedisConstants.USER_DATA_PREFIX, userId);
            if (!redisTemplate.hasKey(key)) {
                redisTemplate.delete(key);
                return fail(exchange, "登录已过期");
            } else {
                Object redisToken = redisTemplate.opsForHash().get(key, LoginConstants.RedisConstants.TOKEN_STR);
                if (redisToken == null || "".equals(redisToken) || !redisToken.equals(token)) {
                    return fail(exchange, "安全认证已失效，请重新登录");
                }
                redisTemplate.expire(key, expTime, TimeUnit.MINUTES);
            }

            String menuKey = String.join(":", LoginConstants.RedisConstants.USER_DATA_PREFIX, userId);
            List<Map> menus = (List<Map>) redisTemplate.opsForHash().get(menuKey, LoginConstants.RedisConstants.USER_MENUS);
            Long timeout = expiresAt - System.currentTimeMillis();

            if (timeout < 1000 * 5 * 60) {
                String newToken = JWTUtil.createToken(payload.getClaimsJson(), LoginConstants.JWT_SECRET_KEY);
                ServerHttpRequest newRequest = request.mutate().header(SystemConstants.AUTHORIZATION, newToken).build();
                ServerWebExchange newServerWebExchange = exchange.mutate().request(newRequest).build();
                redisTemplate.opsForValue().set(key, newToken);
                redisTemplate.expire(key, expTime, TimeUnit.MINUTES);
                return chain.filter(newServerWebExchange);
            }

            if ("GET".equals(method) || uri.endsWith("/page") || uri.endsWith("/list")) {
                return chain.filter(exchange);
            }

            SysLogRecordGatewayDTO dto = new SysLogRecordGatewayDTO();
            dto.setToken(token);
            dto.setMethod(method);
            dto.setUrl(uri);
            kafkaHelper.sendMessage(KafkaTopicConstants.SysLogRecord.TOPIC, JSONObject.toJSONString(dto));
            log.info("网关日志记录：{}", dto);
            return chain.filter(exchange);
        }

        @Override
        public int getOrder() {
            return 0;
        }

        private Mono<Void> fail(ServerWebExchange exchange, String msg) {
            Map<String, Object> message = new HashMap<>();
            message.put("code", HttpStatus.UNAUTHORIZED.value());
            message.put("msg", msg);
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            DataBuffer buffer = response.bufferFactory().wrap(JSONObject.toJSONString(message).getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Flux.just(buffer));
        }

    }


    @Data
    public static class Config {
        private boolean enabled;
    }
}
```

## 全局过虑器示例
```java
@Slf4j
@Component
public class LogFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info(exchange.getRequest().getPath().value());
        return chain.filter(exchange);
    }
}

```
