# ORM 框架
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
            <artifactId>paas-mybatis-plus-starter</artifactId>
        </dependency>
    </dependencies>
```

# 配置文件
```yaml
spring:
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

```
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

