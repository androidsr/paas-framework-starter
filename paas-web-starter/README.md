# SSM 基础脚手架
ssm基础框架在基于spring boot +mybatis-plus基础之前进行了常见问题解决方案进行了封装处理，为项目提供一个简单实用的基础框架的同时，又不做过多的限制，项目组也可按实际项目需求进行二次封装处理。
# maven依赖
```xml
    <parent>
        <groupId>paas.framework.starter</groupId>
        <artifactId>paas-framework-starter</artifactId>
        <version>2.0-SNAPSHOT</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>paas.framework.starter</groupId>
            <artifactId>paas-web-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>paas.framework.starter</groupId>
            <artifactId>paas-model</artifactId>
        </dependency>
        <dependency>
            <groupId>paas.framework.starter</groupId>
            <artifactId>paas-tools</artifactId>
        </dependency>
        <dependency>
            <groupId>paas.framework.starter</groupId>
            <artifactId>paas-mybatis-plus-starter</artifactId>
        </dependency>
    </dependencies>
```

# 配置文件
```yaml
server:
  port: 9092
spring:
  application:
    name: application-name
  profiles:
    active: dev
  mvc:
    pathmatch:
      matching-strategy: ANT_PATH_MATCHER
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    hikari:
      minimum-idle: 10
      maximum-pool-size: 20
      auto-commit: true
      #idle-timeout: 900
      pool-name: hikari-db-pool
      max-lifetime: 0
      #connection-timeout: 60000
      connection-test-query: SELECT 1
  aop:
    auto: true
    proxy-target-class: true
  cache:
    type: simple
  http:
    encoding:
      enabled: true
      charset: UTF-8
      force: true
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: GMT+8
  servlet:
    multipart:
      enabled: true
      max-file-size: 50MB
logging:
  level:
    root: info
    com.alibaba.nacos: warn
  config:
    classpath: logging-config.xml

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
# 功能介绍
## web内置功能

1. 统一异常处理，默认集成统一异常处理。包含未知异常处理，业务异常拦截（BusException），参数验证异常拦截，报文格式错误异常拦截等。
2. swagger兼容配置。
3. 日期输入处理，支持json字符串日期格式输入序列化。jdk8 LocalDate 序列化集成。
4. 常用工具类SpringUtils，WebUtils等。
5. 枚举参数输入序列化转换

## ORM内置功能
```yaml
paas:
  mybatis-plus:
    db-type: MYSQL  ##数据库类型
```
BaseMapper扩展功能
```
    /**
     * 通过ID判断是否存在
     * 默认column id
     *
     * @param id id值
     * @return
     */
    default boolean exists(Serializable id) 

    /**
     * 通过ID判断是否存在
     *
     * @param column 主键ID
     * @param value  id值
     * @return
     */
    default boolean exists(String column, Serializable value)

    /**
     * 通过ID判断是否存在
     *
     * @param columns 主键ID
     * @param values  id值
     * @return
     */
    default boolean exists(String[] columns, Serializable... values) 

    /**
     * 单条件查询集合
     *
     * @param column 查询数据库列
     * @param value  查询条件值
     * @return
     */
    default List<T> selectList(String column, Serializable value) 

    /**
     * 查询一条数据
     *
     * @param column 查询数据库列
     * @param value  查询条件值
     * @return
     */
    default T selectOne(String column, Serializable value) 

    /**
     * 查询多条数据
     *
     * @param columns 查询数据列
     * @param values  查询条件值
     * @return
     */
    default List<T> selectList(List<String> columns, Serializable... values) 

    /**
     * 查询一条数据
     *
     * @param columns 查询数据列
     * @param values  查询条件值
     * @return
     */
    default T selectOne(List<String> columns, Serializable... values)


    /**
     * 查询数据集合
     *
     * @param column in查询列
     * @param values in值集合
     * @return
     */
    default List<T> selectList(List<String> columns, Object... values) 

    /**
     * in查询数据集合
     *
     * @param column in查询列
     * @param values in值集合
     * @return
     */
    default List<T> selectIn(String column, Collection<?> values) 

    /**
     * 删除操作（指定字段列和每一列对应和值）
     *
     * @param columns 删除条件列
     * @param values  删除条件值
     * @return
     */
    default Integer delete(String column, Serializable key)

    /**
     * 删除操作（指定字段列和每一列对应和值）
     *
     * @param columns 删除条件列
     * @param values  删除条件值
     * @return
     */
    default Integer delete(String column, Collection<?> values)

    /**
     * 删除操作（指定字段列和每一列对应和值）
     *
     * @param columns 删除条件列
     * @param values  删除条件值
     * @return
     */
    default Integer delete(List<String> columns, Object... values) 

    /**
     * 将查询出的多行数据转换成map（id转名称使用）
     *
     * @param column 查询条件列
     * @param values 查询条件值
     * @param col    id列和名称列
     * @return
     */
    default Map<Object, Object> convert(String column, Set<?> values, String... col)

    /**
     * 将查询出的多行数据转换成map（id转名称使用）
     *
     * @param column 查询条件列
     * @param values 查询条件值
     * @param col    id列和名称列
     * @return
     */
    default Map<String, String> convertString(String column, Set<?> values, String... col)

    /**
     * 批量插入
     *
     * @param list
     */
    void batchAdd(@Param("list") List<T> list);

    /**
     * 批量修改
     *
     * @param list
     */
    void batchUpdate(@Param("list") List<T> list);

    /**
     * 扩展方法
     *
     * @param query 查询条件
     * @return 下拉选择数据
     */
    default List<SelectVO> queryList(@Param("page") IPage page, @Param("query") SelectQueryDTO query, @Param("key") String key, @Param("name") String name) {
        return queryList(page, query, key, name, "");
    }

    /**
     * 分页下拉查询指定简单列返回分页数据
     *
     * @param page     分页参数
     * @param query    查询条件
     * @param key      id列
     * @param name     名称列
     * @param supperId 上级id
     * @return
     */
    List<SelectVO> queryList(@Param("page") IPage page, @Param("query") SelectQueryDTO query, @Param("key") String key, @Param("name") String name, @Param("supperId") String supperId);

    /**
     * 通用查询
     *
     * @param columns 返回列信息
     * @param page    分页查询
     * @param ew      查询条件
     * @return
     */
    List<Map<String, Object>> selectQuery(@Param("page") IPage page, @Param(Constants.WRAPPER) Wrapper<T> ew, @Param("columns") String... columns);
}

//数据库对象父类扩展
public class BaseEntity extends paas.mybatis.BaseEntity {
    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     */
    @TableField(value = "modify_by", fill = FieldFill.INSERT_UPDATE)
    private String modifyBy;
    /**
     * 创建用户姓名
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createByName;
    /**
     * 更新用户姓名
     */
    @TableField(value = "modify_by", fill = FieldFill.INSERT_UPDATE)
    private String modifyByName;
}
```

内置自动填充配置
```java


/**
 * 自动填充数据源配置
 *
 * @author sirui
 */
@Slf4j
@Component
public class MybatisPlusFillSourceConfig implements MybatisPlusFillSource {
    /**
     * 插入时填充的字段属性名
     * @return
     */
    @Override
    public List<String> createFillField() {
        return Arrays.asList("createTime", "updateTime", "createBy", "updateBy");
    }
    /**
     * 修改时填充的字段属性名
     * @return
     */
    @Override
    public List<String> updateFillField() {
        return Arrays.asList("updateTime", "updateBy");
    }

    @Override
    public Map<String, MyBatisFillItem> getData() {
        Map<String, MyBatisFillItem> config = new HashMap<>();
        
        /*if (PaasUtils.isNotEmpty(userId)) {
            config.put("createBy", new MyBatisFillItem(userId, String.class));
            config.put("updateBy", new MyBatisFillItem(userId, String.class));
        }*/
        config.put("createTime", new MyBatisFillItem(new Date(), Date.class));
        config.put("updateTime", new MyBatisFillItem(new Date(), Date.class));
        return config;
    }
}

//数据库对象父类扩展

@Data
public class BaseEntity extends IdEntity {
    /**
     * @ignore 创建时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "create_time", fill = FieldFill.INSERT_UPDATE)
    private Date createTime;
    /**
     * @ignore 更新时间
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    /**
     * @ignore 创建者
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "create_by", fill = FieldFill.INSERT_UPDATE)
    private String createBy;
    /**
     * @ignore 更新者
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}

```
## 动态分表
启动动态分表，并指定表按请求header中自定义参数进行表数据隔离（此处按租户进行表隔离）。支持【#date.YYYYMMDD】按日期格式进行分表隔离
```yaml
paas:
  dynamic-table:
  enable: true
  tables: 
    - yq_coll_info:#header.tenement-alias
    - yq_comments:#header.tenement-alias
    - yq_coll_service_type:#header.tenement-alias
    - yq_coll_content:#header.tenement-alias
```
分表使用自定义设置操作表前缀（DynamicTableConfig.set(dto.getTenementAlias());）。注意在finally中移除（DynamicTableConfig.remove();）ThreadLocal变量，默认支持子线程传递。
```yaml
@Override
    public boolean dailyStatisticalRefresh(JobRefreshCacheDTO dto) {
        String scenicCode = dto.getScenicCode();
        DynamicTableConfig.set(dto.getTenementAlias());
        try {
            Date dataDate = dto.getDataDate();
            dayVolumeSave(scenicCode, dataDate);
            dataCollRankingSave(scenicCode, dataDate);
            dataCommentRankingSave(scenicCode, dataDate);
            collEmotionGroupSave(scenicCode, dataDate);
            collEmotionMediaDaysSave(scenicCode, dataDate);
            commentGroupSave(scenicCode, dataDate);
            commentMediaGroupSave(scenicCode, dataDate);
            wordRankingSave(scenicCode, dataDate);
            collectLogSave(scenicCode, dataDate);
        } finally {
            DynamicTableConfig.remove();
        }
        return true;
    }
```
可通过拦截器进行统一处理配置，以实际功能设计为准。
```java

    @Around("webCommon()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Long start = System.currentTimeMillis();
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        WebUtils.set(request);

        String tenantId = request.getHeader(SystemConstants.HEADER_TENEMENT_ALIAS);
        DynamicTableConfig.set(tenantId);
        Object result = joinPoint.proceed();
        DynamicTableConfig.remove();
        log.info("请求地址：{} - 执行时间：{}", request.getRequestURL().toString(), System.currentTimeMillis() - start);
        return result;
    }
```
## 工具类库
### HttpResult
http接口响应包装类，提供基础静态方法。控制对象只在Controller 层出现。避免在业务层使用。
```java

    public static HttpResult ok(Object data) {
        HttpResult r = new HttpResult();
        r.setData(data);
        r.setCode(ResultMessage.SUCCESS.getCode());
        r.setMsg(ResultMessage.SUCCESS.getMessage());
        return r;
    }

    public static HttpResult isOk(boolean flag) {
        return flag ? ok() : fail((ResultCode)ResultMessage.FAIL);
    }

```
失败响应结果可自定义code，msg。推荐使用ResultCode接口下ResultMessage类对象定义统一响应码，可自动在common中增加。也可自己实现ResultCode接口，自定义响应码信息。
```java
    public static HttpResult fail(int code, String msg) {
        HttpResult r = new HttpResult();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }

    public static HttpResult fail(String msg) {
        HttpResult r = new HttpResult();
        r.setCode(ResultMessage.UNKNOW_USER_ERROR.getCode());
        r.setMsg(msg);
        return r;
    }

    public static HttpResult fail(ResultCode result) {
        HttpResult r = new HttpResult();
        r.setCode(result.getCode());
        r.setMsg(result.getMessage());
        return r;
    }

    public static HttpResult fail(BusException e) {
        HttpResult r = new HttpResult();
        r.setCode(e.getCode());
        r.setMsg(e.getMessage());
        return r;
    }
```
### PageResult
数据库分页对象。Controller定义时使用泛型指定明确返回类型，方便生成文档注释。
```java
    @Override
    @ApiOperation(value = "近期趋势详情列表-舆情检测（日趋势）", notes = "page,start and end time, limit, mediaName")
    public HttpResult<PageResult<TreeMap<String, String>>> queryTrendPage(@RequestBody BigPageQueryDTO query) {
        return HttpResult.ok(bigPageService.queryTrendPage(query));
    }
```
```java
PageResult<Map<String, Object>> queryTrendPage(BigPageQueryDTO query)
```
### MybatisHandler
分页处理工具类。将入参查询对象Page类转换IPage分页对象。返回时将响应结果转换成PageResult对象。
```java
    @Override
    public PageResult<SysRolesVO> queryPage(SysRolesQueryDTO query) {
        IPage<SysRoles> page = MybatisHandler.convert(query.getPage());
        List<SysRoles> data = sysRolesMapper.queryPage(page, query);
        List<SysRolesVO> result = PaasUtils.copyListTo(data, SysRolesVO::new);
        return MybatisHandler.convert(page, result);
    }
```
### SelectQueryDTO
下拉请求参数，分页信息或查询条件。统一固定参数与前端组件保持固定统一的请求和响应标签名。支持按value或label查询。
```java
	@ApiOperation("分页下拉查询")
    @PostMapping("/list")
    HttpResult<PageResult<SelectVO>> queryList(@RequestBody SelectQueryDTO query);
```
### SelectVO
分页下拉选择响应对象，固定对象，label,value 标签。
```java
    @Override
    public PageResult<SelectVO> queryList(SelectQueryDTO query) {
        IPage<SelectVO> page = MybatisHandler.convert(query.getPage());
        List<SelectVO> data = sysMenusMapper.queryList(page, query);
        List<SelectVO> result = PaasUtils.copyListTo(data, SelectVO::new);
        return MybatisHandler.convert(page, result);
    }
```
### PaasUtils
常用对象处理工具类库。
数据对象转换成VO对象
```java
    @Override
    public SysUsersVO selectById(Serializable id) {
        SysUsersVO result = PaasUtils.copy(sysUsersMapper.selectById(id), SysUsersVO::new);
        result.setLoginPassword("");
        return result;
    }
```
DTO对象转换成数据库对象
```java
    @Override
    public int save(SysUsersDTO dto) {
        SysUsers entity = PaasUtils.copy(dto, SysUsers::new);
        return sysUsersMapper.insert(entity);
    }
```
更新时不复制空的字段信息
```java
    @Override
    public int updateById(SysUsersDTO dto) {
        SysUsers entity = sysUsersMapper.selectById(dto.getId());
        Asserts.isTrueError(entity == null, ResultMessage.NOT_FOUND);
        PaasUtils.copyIgnoreNull(dto, entity);
        entity.setLoginPassword(null);
        return sysUsersMapper.updateById(entity);
    }
```
List<DB>集合对象转换成List<VO>集成对象
```java
    @Override
    public PageResult<SysUsersVO> queryPage(SysUsersQueryDTO query) {
        IPage<SysUsers> page = MybatisHandler.convert(query.getPage());
        List<SysUsers> data = sysUsersMapper.queryPage(page, query);
        List<SysUsersVO> result = PaasUtils.copyListTo(data, SysUsersVO::new);
        return MybatisHandler.convert(page, result);
    }
```
List大集合拆分成小集合
```java
    @Override
    @Transactional
    public HttpResult batchAdd(List<SysUsers> data) {
        List<List<SysUsers>> items = PaasUtils.splitList(data, PaasUtils.BATCH_ADD_SIZE);
        for (List<SysUsers> item : items) {
            sysUsersMapper.batchAdd(item);
        }
        return HttpResult.ok();
    }
```
常用非空判断，支持List,String,Map,及IsNotEmpty()。少用！符做非判断不方便阅读。
```java
    public HttpResult<GetUserInfoVO> getUserInfo(@RequestBody GetUserInfoDTO dto) {
        Map<String, String> params = new HashMap<>();
        if (PaasUtils.isNotEmpty(dto.getUserId())) {
            params.put("userId", dto.getUserId());
        }
    }

   @Override
    public AnalysisSubTaskResultVO subHandler(CommentMessageDTO dto, YqComments info) {
        List<YqCollServiceType> result = new ArrayList();
        String content = info.getContent();
        if (PaasUtils.isEmpty(content)) {
            return result;
        }
   }
```
```java
    @Override
    @RedisLock(key = "#scenicCode", waitTime = 3, timeUnit = TimeUnit.MINUTES)
    public void handler(String scenicCode, String tenementAlias, Date startDate) {
        DynamicTableConfig.set(tenementAlias);
        try {
            List<YqCollInfo> pendingTemp = yqCollInfoService.findSimilarityToday(tenementAlias, startDate);
            if (PaasUtils.isEmpty(pendingTemp)) {
                return;
            }
        }
 }
```
### Asserts
业务断言操作类。
条件为true时抛出异常结束业务流程。
```java
    @Override
    public int updateById(SysMenusDTO dto) {
        SysMenus entity = sysMenusMapper.selectById(dto.getId());
        Asserts.isTrueError(entity == null, ResultMessage.NOT_FOUND);
        PaasUtils.copyIgnoreNull(dto, entity);
        return sysMenusMapper.updateById(entity);
    }
```
条件为false时抛出异常结束业务流程。
```java
Asserts.isFalseError(PaasUtils.isNotEmpty(jsonStr), BaiduAiMessage.FAIL_INVALID);
```
判断feign接口响应是否成功，成功时：返回数据data。失败时：异常结束业务流程
```java
HttpResult<List<YqTenementFilterVO>> httpResult = tenementFalterFacade.findByScenicCodeAndChannelId(filterQuery);
Asserts.feignResultVerify(httpResult);
```
判断feign接口响应是否成功，成功时：true,。失败时：false
```java
HttpResult<Long> httpResult = bigMonitoringFacade.findCount(BigMonitoringQueryDTO.builder().build());
if (Asserts.feignIsSuccess(httpResult)) {
    result.setCount(httpResult.getData());
} else {
    result.setCount(0L);
}
```
判断feign接口响应是否成功并data不为空。成功时：true,。失败时：false
```java
HttpResult<YqDataChannelInfoVO> vo = yqDataChannelInfoFacade.findById(item.getChannelId());
YqDataChannelInfoVO channel;
if (Asserts.feignIsDataSuccess(vo)) {
    channel = vo.getData();
} else {
    continue;
}
```
### WebUtils
获取/设置请求头header参数信息。
```java
String token = WebUtils.getHeaderValue(SystemConstants.HEADER_AUTHORIZATION);
```
### DateUtils
日期工具类包含常规日期处理（格式转换，字符串转日期，日期转换字符，日期运算等）
以命名Date结尾处理日期。以DateTime结尾处理日期时间。
格式化格式定义：DateEnum，DateTimeEnum包含常用格式字符串。
```java
/**
     * 获取当前日期
     *
     * @return （年月日 00:00:00）
     */
    public static Date getDate()

    /**
     * 获取当前日期
     *
     * @return （年月日 时分秒）
     */
    public static Date getDateTime()

    /**
     * 获取当前日期字符串
     *
     * @param pattern 日期格式
     * @return 字符串：yyyy-MM-dd 或 yyyyMMdd
     */
    public static String getDate(DateEnum pattern)

    /**
     * 获取当前日期字符串
     *
     * @return 字符串：yyyy-MM-dd 或 yyyyMMdd
     */
    public static Date getDate(Date date)

    /**
     * 获取当前日期字符串
     *
     * @param pattern 日期格式
     * @return 字符串：yyyy-MM-dd HH:mm:ss  或 yyyyMMddHHmmss
     */
    public static String getDateTime(DateTimeEnum pattern)

    /**
     * 获取当前日期字符串
     */
    public static Date getDateTime(Date date)

    /**
     * 指定日期格式化字符串
     *
     * @param date    指定日期
     * @param pattern 日期格式
     * @return 字符串：yyyy-MM-dd 或 yyyyMMdd
     */
    public static String formatDate(LocalDate date, DateEnum pattern)

    /**
     * 指定日期时间格式化字符串
     *
     * @param dateTime 指定日期时间
     * @param pattern  日期格式
     * @return 字符串：yyyy-MM-dd HH:mm:ss 或 yyyyMMddHHmmss
     */
    public static String formatDateTime(LocalDateTime dateTime, DateTimeEnum pattern)

    /**
     * 指定日期时间格式化字符串
     *
     * @param dateTime 指定日期时间
     * @param pattern  日期格式
     * @return 字符串：yyyy-MM-dd HH:mm:ss 或 yyyyMMddHHmmss
     */
    public static String formatDateTime(Date dateTime, DateTimeEnum pattern)

    /**
     * 指定日期时间格式化字符串
     *
     * @param date    指定日期时间
     * @param pattern 日期格式
     * @return 字符串：yyyy-MM-dd 或 yyyyMMdd
     */
    public static String formatDate(Date date, DateEnum pattern)

    /**
     * 字符串格式化为日期
     *
     * @param text    字符串
     * @param pattern 字符串格式： yyyy-MM-dd 或 yyyyMMdd
     * @return （年月日 00:00:00）
     */
    public static Date parseDate(CharSequence text, DateEnum pattern)

    /**
     * 字符串格式化为日期时间
     *
     * @param text    字符串
     * @param pattern 字符串格式：yyyy-MM-dd HH:mm:ss 或 yyyyMMddHHmmss
     * @return（年月日 时分秒）
     */
    public static Date parseDateTime(CharSequence text, DateTimeEnum pattern)

    /**
     * LocalDate 转换 Date
     *
     * @param localDate 日期
     * @return
     */
    public static Date toDate(LocalDate localDate)


    /**
     * LocalDateTime 转换 Date
     *
     * @param localDateTime 日期
     * @return
     */
    public static Date toDateTime(LocalDateTime localDateTime)


    /**
     * Date 转换 LocalDate
     *
     * @param date 日期
     * @return
     */
    public static LocalDate toLocalDate(Date date)


    /**
     * Date 转换 LocalDateTime
     *
     * @param date 日期
     * @return
     */
    public static LocalDateTime toLocalDateTime(Date date)

    /**
     * LocalDate 转换 LocalDateTime
     *
     * @param localDate
     * @return
     */
    public static LocalDateTime dateToDateTime(LocalDate localDate)

    /**
     * LocalDateTime 转换 LocalDate
     *
     * @param localDateTime
     * @return
     */
    public static LocalDate dateTimeToDate(LocalDateTime localDateTime)

    /**
     * 日期加N天
     *
     * @param date 日期
     * @param days 增加天数
     * @return
     */
    public static LocalDate dateAddDays(LocalDate date, int days)

    /**
     * 日期减N天
     *
     * @param date 日期
     * @param days 增加天数
     * @return
     */
    public static LocalDate dateMinusDays(LocalDate date, int days)


    /**
     * 日期增运算
     *
     * @param dateTime 日期时间
     * @param size     加多少
     * @param unit     单位
     * @return
     */
    public static Date dateAdd(Date dateTime, int size, TimeUnit unit)

    /**
     * 日期增运算
     *
     * @param dateTime 日期时间
     * @param size     加多少
     * @param unit     单位
     * @return
     */
    public static LocalDateTime dateAdd(LocalDateTime dateTime, int size, TimeUnit unit)

    /**
     * 日期减运算
     *
     * @param dateTime 日期时间
     * @param size     减多少
     * @param unit     单位
     * @return
     */
    public static Date dateMinus(Date dateTime, int size, TimeUnit unit)

    /**
     * 日期减运算
     *
     * @param dateTime 日期时间
     * @param size     减多少
     * @param unit     单位
     * @return
     */
    public static LocalDateTime dateMinus(LocalDateTime dateTime, int size, TimeUnit unit)

    /**
     * 获取一天开始时间
     *
     * @param date
     * @return YYYY-MM-DD 00:00:00
     */
    public static Date getStartDate(Date date)

    /**
     * 获取一天结果时间
     *
     * @param date
     * @return YYYY-MM-DD 23:59:59
     */
    public static Date getEndDate(Date date)

    /**
     * 相差多少天
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int dayDiff(Date startDate, Date endDate)

    /**
     * 相差
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static long dateDiff(Date startDate, Date endDate)

```

 
