# MgongoDB
基于spring data mongodb 集成，提供基础工具类，不满足情况下可自行基于MongoTemplate自行定义使用。
## maven依赖
```xml
<dependency>
	<groupId>paas.framework.starter</groupId>
	<artifactId>paas-mongo-starter</artifactId>
</dependency>
```
## yml配置
```yaml
spring:
  data:
    mongodb:
      host: 127.0.0.1
      port: 27017
      database: db
```

## 操作工具类
```java

    /**
     * 保存对象List到指定集合中
     * <p>
     * 也可以在实体类上使用@Document(collection=“集合名称”)指定集合名称，未指定则默认实体类的类名为集合名称
     *
     * @param entitys
     */
    public void saveAll(String collName, List<?> entitys) 

    /**
     * 保存单个对象到指定集合中
     *
     * @param collName 集合名称
     * @param entity   实体名称
     */
    public void saveOne(String collName, Class<?> entity) 

    /**
     * 根据id倒序查询 集合中的数据
     *
     * @param entity    数据实体
     * @param collName  集合名称
     * @param direction 倒序/正序 Direction.DESC/ASC
     * @param
     */
    public Object findSortById(Class<?> entity, String collName, Sort.Direction direction) 

    /**
     * 查询返回指定字段
     *
     * @param fields   需要返回的指定字段  eg: fields.add("runTime");
     * @param clazz    数据实体类class
     * @param collName 集合名称
     * @param map      Map<查询条件key,查询条件value>  eg: map.put("describe", "查询用户信息");
     * @param returnId 返回字段的时候id默认为返回，不返回id则field设置
     * @return
     */
    public Object findDesignField(List<String> fields, Map<String, Object> map, Class<?> clazz, String collName, boolean returnId)

    /**
     * 查询指定集合中的所有数据
     *
     * @param entity   数据实体类
     * @param collName 集合名称
     */
    public Object findAll(Class<?> entity, String collName)

    /**
     * 模糊查询 根据 key 可以到 collName 中进行模糊查询 并排序
     *
     * @param param     匹配的参数名称
     * @param key       模糊搜索关键字
     * @param collName  集合名称
     * @param sortField 排序字段
     * @param direction Direction.desc /asc 倒序/正序
     * @return java.lang.Object
     **/
    public Object findLike(String param, String key, String collName, String sortField, Sort.Direction direction, Class<?> cls)

    /**
     * 向指定集合设置索引
     *
     * @param collName  集合名称
     * @param indexName 索引名称
     * @param map       map.put("添加索引的字段",Direction.ASC/DESC)
     */
    public void createIndex(String collName, String indexName, Map<String, Sort.Direction> map)

    /**
     * 获取指定集合中的索引信息
     *
     * @param collName 集合名称
     * @return
     */
    public Object getIndexInfo(String collName) {
        return mongoTemplate.indexOps(collName).getIndexInfo();
    }


    /**
     * 根据索引名称删除索引
     *
     * @param indexName 索引名称
     * @param collName  集合名称
     */
    public void removeIndexByName(String collName, String indexName) 


    /**
     * 删除指定集合中得所有索引
     *
     * @param collName 集合名称
     */
    public void removeIndexByName(String collName)


    /**
     * 根据指定key 和value到指定collName集合中删除数据
     *
     * @param key
     * @param value
     * @param collName
     */
    public void removeAllByParam(String key, String value, String collName)


    /**
     * 根据指定条件查询 并排序
     *
     * @param obj      数据对象
     * @param map      Map<"查询条件key"，查询条件值> map
     * @param collName 集合名称
     * @return
     */
    public List<? extends Object> findSortByParam(Class<?> obj, String collName, Map<String, Object> map, String sortField, Sort.Direction direction)


    /**
     * 范围查询
     * <p>
     * 查询大于等于begin  小于等于end范围内条件匹配得数据并排序
     *
     * @param obj           数据对象
     * @param collName      集合名称
     * @param map           Map<"查询条件key"，查询条件值> map
     * @param sortField     排序字段
     * @param direction     排序方式  Direction.asc   / Direction.desc
     * @param rangeCriteria 示例： lt小于  lte 小于等于  gt大于  gte大于等于 eq等于 ne不等于
     *                      <p>
     *                      Criteria rangeCriteria=Criteria.where("createDate").gte(begin).lte(end));
     *                      <p>
     *                      createDate:数据库中的时间字段，gegin:起始时间  end:结束时间
     * @return
     */
    public List<? extends Object> findRangeByParam(Class<?> obj, String collName, Map<String, Object> map, String sortField, Sort.Direction direction, Criteria rangeCriteria) 


    /**
     * 根据指定key value到指定集合中查询匹配得数量
     *
     * @param collName
     * @param key
     * @param value
     * @return
     */
    public long count(String collName, String key, String value)


    /**
     * 在指定范围内查询匹配条件的数量
     *
     * @param clazz         数据实体类
     * @param collName      集合名称
     * @param map           查询条件map
     * @param rangeCriteria 范围条件  Criteria rangeCriteria= Criteria.where("数据库字段").gt/gte（起始范围）.lt/lte（结束范围）
     * @return
     */
    public Long countRangeCondition(Class<?> clazz, String collName, Criteria rangeCriteria, Map<String, Object> map)


    /**
     * 指定集合 根据条件查询出符合的第一条数据
     *
     * @param clazz    数据对象
     * @param map      条件map  Map<条件key,条件value> map
     * @param collName 集合名
     * @return
     */
    public Object findSortFirst(Class<?> clazz, Map<String, Object> map, String collName, String field, Sort.Direction direction)


    /**
     * 指定集合 修改数据，且修改所找到的所有数据
     *
     * @param accordingKey   修改条件 key
     * @param accordingValue 修改条件 value
     * @param map            Map<修改内容 key数组,修改内容 value数组>
     * @param collName       集合名
     * @param type           修改操作类型  1:修改第一条数据  0：修改所有匹配得数据
     */
    public void updateMulti(String accordingKey, Object accordingValue, Map<String, Object> map, String collName, Integer type)

    /**
     * 对某字段做sum求和
     *
     * @param clazz         数据实体类
     * @param map           Map<查询条件key,查询条件value> map
     * @param collName      集合名称
     * @param sumField      求和字段
     * @param rangeCriteria 范围条件
     * @return Criteria rangeCriteria = Criteria.where(字段).gt(起始范围).lt(结束范围)
     */
    public Object findSum(Class<?> clazz, Map<String, Object> map, String collName, String sumField, Criteria rangeCriteria) 
    /**
     * 分页查询
     *
     * @param clazz     数据实体类
     * @param collName  集合名称
     * @param map       Map<"查询条件key"，查询条件值> map 若 keys/values 为null,则查询集合中所有数据
     * @param pageNo    当前页
     * @param pageSize  当前页数据条数
     * @param direction Direction.Desc/ASC 排序方式
     * @param sortField 排序字段
     * @return
     */
    public PageResult findSortPage(Class<?> clazz, String collName, Map<String, Object> map, int pageNo, int pageSize, Sort.Direction direction, String sortField) 
```
#### 
