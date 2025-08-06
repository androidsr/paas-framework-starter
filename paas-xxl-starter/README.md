# XXL-JOB分布式定时任务
## maven依赖
```xml
<dependency>
	<groupId>paas.framework.starter</groupId>
	<artifactId>paas-xxl-starter</artifactId>
</dependency>
```
## yml配置
```yaml
xxl:
  job:
    admin:
      addresses: http://127.0.0.1:8048/xxl-job-admin
    accessToken: 
    executor:
      appname: xxxxxx-task-web
      ip:
      port: 9099
      logpath: /xxl-job/applogs/xxl-job/jobhandler
      address: 
      logretentiondays: 10
```
## 示例代码
```java
@Slf4j
@Component
public class CollectionHandler {

    /**
     * 数据采集定时任务
     *
     * @return
     * @throws Exception
     */
    @XxlJob("collectionHandler")
    public HttpResult<String> collectionHandler() throws Exception {
		return HttpResult.ok();
    }
}
```

