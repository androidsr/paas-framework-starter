# sentinel流控组件
流控组件站时未对持久化方案进行验证，已存储nacos持久化组件库。
## maven依赖
```xml
<dependency>
	<groupId>paas.framework.starter</groupId>
	<artifactId>paas-sentinel-starter</artifactId>
</dependency>
```
## yml配置
```yaml
spring:
  cloud:
    sentinel:
      transport:
        dashboard: 127.0.0.1:8080
      eager: true
```

