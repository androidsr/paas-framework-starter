# Nacos注册中心/配置中心
## maven依赖
```xml
<dependency>
	<groupId>paas.framework.starter</groupId>
	<artifactId>paas-nacos-starter</artifactId>
</dependency>
```
## yml配置
```yaml
spring:
  cloud:
    nacos:
      discovery:
        enabled: true
        server-addr: 127.0.0.1:8848
        namespace: "dev"
        group: "DEFAULT_GROUP"
```
```yaml
spring:
  cloud:
    nacos:
      config:
        enabled: true
        refresh:
          enabled: true
        server-addr: 127.0.0.1:8848
        namespace: "dev"
        group: "DEFAULT_GROUP"
        file-extension: yaml
        shared-configs[0]:
          data-id: 公共配置.yaml
          refresh: true

```
### 
