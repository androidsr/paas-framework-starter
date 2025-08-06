# Feign微服务调用
1. Feign集成okhttp组件并集成sentinel组件
2. 对统一异常处理进行了定义，可在熔断阶级和统一异常处理中进行切换选择，推荐熔断机制
3. 通过平台参数配置，可以复制header中的头信息至远程服务
4. 重试规则进行了配置，默认关闭重试，可在feign中进行配置重试，配置重试时注意接口幂等性处理
## maven依赖
```xml
<dependency>
	<groupId>paas.framework.starter</groupId>
	<artifactId>paas-openfeign-starter</artifactId>
</dependency>
```
## yml配置
```yaml
feign:
  sentinel:
    enabled: true
  okhttp:
    enabled: true
  httpclient:
    enabled: false
    max-connections: 50
    connect-timeout: 5000
    ok-http:
      read-timeout: 20
  client:
    config:
      default:
        loggerLevel: FULL #BASIC
  compression:
    request:
      enabled: true
      min-request-size: 4096
      mime-types: text/xml,application/xml,application/json
    response:
      enabled: true
      min-request-size: 4096
      mime-types: text/xml,application/xml,application/json
```
## 框架配置
```yaml
paas:
  feign:
    header-key:    ##feign header参数复制
      - Authorization
      
```
## 示例代码
```xml
<distributionManagement>
  <snapshotRepository>
    <id>snapshots</id>
    <name>Nexus Snapshots Repository</name>
    <url>https://packages.aliyun.com/maven/repository/2398536-snapshot-MeD5lZ</url>
  </snapshotRepository>
  <repository>
    <id>releases</id>
    <name>Nexus snapshots Repository</name>
    <url>https://packages.aliyun.com/maven/repository/2398536-release-SwanT6</url>
  </repository>
</distributionManagement>

<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>paas.framework.starter</groupId>
      <artifactId>paas-framework-starter</artifactId>
      <version>2.0-SNAPSHOT</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
  </dependencies>
</dependencyManagement>

<dependencies>
  <dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
  </dependency>
  <dependency>
    <groupId>paas.framework.starter</groupId>
    <artifactId>paas-openfeign-framework-starter</artifactId>
    <version>2.0-SNAPSHOT</version>
  </dependency>
</dependencies>
```
feign是否重试按自己项目需求而定，重试的情况下需要保证接口幂等处理，建议查询类可进行重试配置，
可进行自定义全局异常处理，非200响应进行全局异常处理。默认采用fallback熔断降级处理。推荐此种方式。
```java

@FeignClient(name = "scm-finance-manage-web", contextId = "SysBusinessParamsFacade", path = "/sys/business/params"
        , configuration = RetryConfig.class
        , fallback = SysBusinessParamsFacadeFallback.class)
public interface SysBusinessParamsFacade {

    @PostMapping("/findByGroupIds")
    HttpResult<List<SysBusinessParamsVO>> findByGroupId(@RequestBody SysBusinessParamsQueryDTO dto);

    @GetMapping("/findByGroupIdAll")
    HttpResult<Map<String, List<SysBusinessParamsVO>>> findByGroupIdAll();
}

```
```java
@FeignClient(name = "scm-finance-manage-web", contextId = "SysBusinessParamsFacade", path = "/sys/business/params"
        , configuration = {FeignErrorDecoder.class}
        , fallback = SysBusinessParamsFacadeFallback.class)
public interface SysBusinessParamsFacade {

    @PostMapping("/findByGroupIds")
    HttpResult<List<SysBusinessParamsVO>> findByGroupId(@RequestBody SysBusinessParamsQueryDTO dto);

    @GetMapping("/findByGroupIdAll")
    HttpResult<Map<String, List<SysBusinessParamsVO>>> findByGroupIdAll();
}
```
熔断降级需要特殊处理进行实现。
```java
@Slf4j
@Component
public class SysBusinessParamsFacadeFallback implements SysBusinessParamsFacade {

    @Override
    public HttpResult findByGroupId(SysBusinessParamsQueryDTO dto) {
        return HttpResult.fail(ResultMessage.NET_FAILURE);
    }

    @Override
    public HttpResult<Map<String, List<SysBusinessParamsVO>>> findByGroupIdAll() {
        return HttpResult.fail(ResultMessage.NET_FAILURE);
    }
}
```
