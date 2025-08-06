# Redis缓存
通过spring boot集成redis方案，进行了扩展，实现自定义注解在指定方法头上设置缓存。不侵入业务代码逻辑。
针对redis序列化规则进行了定义。
针对数据库查询缓存进行了集成。
同时集成redisson进行了集成。
## maven依赖
```xml
<dependency>
  <groupId>paas.framework.starter</groupId>
  <artifactId>paas-redis-starter</artifactId>
</dependency>
```
## 配置文件配置
同spring boot 配置相当，支持集群，路由，单机模式等。
```yaml
  redis:
    database: 9
    timeout: 10000
    host: 127.0.0.1
    port: 6379
    #sentinel:
    #  master: redismaster
    #  nodes:127.0.0.1:26370,127.0.0.2:26370,127.0.0.3:26370
    password: 123456
    lettuce:
      pool:
        max-active: 500
        max-wait: -1
        max-idle: 10
        min-idle: 5

```
框架自定义缓存配置参数，针对查询缓存默认过期时间，启动缓存注解，以及缓存注解拼接类名+方法名前缀，保证微服务环境下各业务服务缓存key冲突。
```yaml
paas:
	redis:
    timeout: 3
    enable: true
    prefix: true
```
## 使用示例
注入方式可通过@Autowired和@Resource 推荐使用@Autowired，不指定类型更方便业务代码针对不同的业务场景进行处理。
```java
@Autowired
RedisTemplate redisTemplate;

@Resource
RedisTemplate<String, YqTenementConfigVO> redisTemplate;
```
### 缓存注解使用
在业务方法上增加注解，并通过Spel表达式指定key（不完全相同）；此处以参数scenicCode，以及query对象下的所有参数进行拼接为一个key。并指定过期时间。
key不建议过长，扩展取header中的参数获取key。
推荐在controller层进行使用注解，上层进行拦截保证业务性能。以实际业务情况自行处理。
注意：未对缓存key进行分布式锁处理，在高并发情况下，会导致缓存失效时大量请求直接请求数据库（常规业务场景并发量无需控制保证性基础功能性能需求）
```java
@RedisCache(key = "#scenicCode:#query", expireTime = CacheTimeoutConstants.MINUTE_5)
public EmotionSumVO findByArticleCount(String scenicCode, StatisticQueryDTO query) 

@RedisCache(key = "#header.scenic-code:#query", expireTime = CacheTimeoutConstants.MINUTE_5)
public HttpResult<List<YqCollInfoVO>> eventList(@RequestBody BigEventQueryDTO query) 

@RedisCache(key = "#header.scenic-code:#id", expireTime = CacheTimeoutConstants.MINUTE_10)
public HttpResult<EventDetailVO> getEventHandleRecords(@PathVariable("id") String id)

@RedisCache(key = RedisKeyConstants.EVENT_TYPE_CONFIG_ANALYSIS_CONFIG_DATA_CACHE + ":#scenicCode", expireTime = 5, timeUnit = TimeUnit.MINUTES)
public List<YqEventTypeConfigVO> findEventConfig(String scenicCode)

@RedisCache(key = RedisKeyConstants.COMMON_DICT_GROUP_ID_ANALYSIS_CONFIG_DATA_CACHE, expireTime = 5, timeUnit = TimeUnit.MINUTES)
public Map<String, CommonDictVO> getConfigDict()
```

### 缓存注解更新方法
使用缓存时不建议进行更新处理，规划操作可通过更新业务数据时进行删除，待下次查询时进行重新加载缓存。此处不对高并发场景下，数据一致性延迟双删进行处理。
针对数据变更时进行删除缓存处理。
```java
@RedisCache(key = RedisKeyConstants.SYS_BUSINESS_PARAMS_KEY, type = CacheTypeEnum.DELETE)
public HttpResult add(@RequestBody @Validated(Insert.class) SysBusinessParamsDTO dto) 

@RedisCache(key = RedisKeyConstants.SYS_BUSINESS_PARAMS_KEY, type = CacheTypeEnum.DELETE)
public HttpResult update(@RequestBody @Validated(Update.class) SysBusinessParamsDTO dto)

@RedisCache(key = RedisKeyConstants.SYS_BUSINESS_PARAMS_KEY, type = CacheTypeEnum.DELETE)
public HttpResult deleteById(@PathVariable("id") Long id) 

@RedisCache(key = RedisKeyConstants.SYS_BUSINESS_PARAMS_KEY, timeUnit = TimeUnit.HOURS)
public HttpResult<Map<String, List<SysBusinessParamsVO>>> findByGroupIdAll() 
```
# Redisson分布式锁
分布式项目配置参考redis，默认与redis同时集成。
## 分布式锁工具类
```java

    /**
     * 分布式自动续约锁
     *
     * @param key      锁定key
     * @param waitTime 尝试加锁等待时间（秒）
     * @return
     */
    public RLock lock(String key, long waitTime)

    /**
     * 分布式不续约锁
     *
     * @param key       锁定key
     * @param waitTime  尝试加锁等待时间（秒）
     * @param leaseTime 锁过期时间（秒）
     * @return
     */
    public RLock lock(String key, long waitTime, long leaseTime)
    public <T> T lock(String key, long waitTime, SuccessCallback callback)

    /**
     * 分布式自动续约锁
     *
     * @param key      锁定key
     * @param waitTime 尝试加锁等待时间（秒）
     * @param callback 业务处理函数(返回结果)
     */
    public <T> T lock(String key, long waitTime, long leaseTime, SuccessCallback callback)
    public <T> T lock(String key, long waitTime, SuccessCallback callback, FailCallback fail)

    /**
     * 分布式不续约锁
     *
     * @param key       锁定key
     * @param leaseTime 锁过期时间（秒）
     * @param waitTime  尝试加锁等待时间（秒）
     * @param callback  成功业务处理函数
     * @param fail      失败业务处理函数
     */
    public <T> T lock(String key, long waitTime, long leaseTime, SuccessCallback callback, FailCallback fail)

```
## 使用示例
```java
@Autowired
LockHelper lockHelper;
```
通过调用工具类方法，实现分布式锁进行处理，内置自动加锁解锁过程。处理逻辑针对成功回调，失败回调进行处理。需要注意的时，通常情况下，加锁失败时，需要对业务逻辑重试进行处理。重试方案以实际项目需求自行设计。
```java
//加锁成功执行业务逻辑
lockHelper.lock("yqCollEventTypeService." + info.getId(), 30, () -> {
    yqCollEventTypeService.insert(finalEntity);
    return null;
});

//加锁成功处理业务并返回结果。
List<YqCollEventType> result = lockHelper.lock(key, 120, () -> {
    List<YqEventTypeConfigVO> eventConfig = commonAnalysis.findEventConfig(info.getScenicCode());
    YqEventTypeConfigVO other = eventConfig.stream().filter(v -> "other".equals(v.getEventType())).findFirst().get();

    //分析新产生的事件
    List<YqCollInfo> beforeTodayData = yqCollInfoService.findBeforeToday(dto.getTenementAlias(), info.getCreateTime());
    List<YqCollInfo> todayData = yqCollInfoMapper.findByCreateTime(DateUtils.getDate(info.getCreateTime()), DateUtils.getEndDate(info.getCreateTime())/*, info.getId()*/);
    beforeTodayData.addAll(todayData);
    List<YqCollEventType> ret = comparisonTitle(info, beforeTodayData, info.getTitle(), eventConfig, other);
    return ret;
});

//加锁成功与失败分别处理逻辑
lockHelper.lock("yqCollServiceTypeService." + info.getId(), 30, () -> {
    yqCollServiceTypeService.insertList(data);
    return null;
}, () -> {
    throw new BusException()
});
```
## 分布式锁注解
通过注解的方式实现分布式锁功能，满足一般性业务需求。通过注解的方式进行分布式锁处理，是对整个方法层进行加锁，粒度更粗，需要保证业务性能的情况下，不建议使用此方式；并且针对加锁失败，默认不进行处理，直接异常结果，需要对加锁失败进行特殊处理的情况下不满足需求。
## 示例代码
分布式锁key同redis类似，规则通过Spel表达式进行定义。并同redis配置一至支持自动拼接类名+方法名为前缀。
```java
@RedisLock(key = "#scenicCode", waitTime = 3, timeUnit = TimeUnit.MINUTES)
public void handler(String scenicCode, String tenementAlias, Date startDate)

@RedisLock
private boolean collEmotionMediaDaysSave
```
