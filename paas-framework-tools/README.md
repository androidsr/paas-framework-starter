## eoss常用类库说明
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
### ExcelUtil
针对EasyExcel进行封装，提供常用生成excel静态方法。
```java

    /**
     * 读取excel数据
     *
     * @param inputStream   数据文件
     * @param cls           EasyExcel实体对象
     * @param callback      回调函数
     * @param headRowNumber 标题所在行
     */
    public static <T> void readExcel(InputStream inputStream, Class<T> cls, EasyListener.ReadCallback callback, Integer headRowNumber) 

    /**
     * 读取excel数据
     *
     * @param inputStream 数据文件
     * @param cls         EasyExcel实体对象
     * @param callback    回调函数
     */
    public static <T> void readExcel(InputStream inputStream, Class<T> cls, EasyListener.ReadCallback callback) 

    /**
     * @param inputStream   数据文件
     * @param cls           EasyExcel实体对象
     * @param callback      回调函数
     * @param headRowNumber 标题所在行
     * @param sheetNos      要读取的shell编号
     */
    public static <T> void readExcelBySheet(InputStream inputStream, Class<T> cls, EasyListener.ReadCallback callback, Integer headRowNumber, int... sheetNos) 

    /**
     * http excel写入
     *
     * @param response http响应对象
     * @param fileName 下载文件名称
     * @param clazz    easy报表对象
     * @param data     数据
     * @throws Exception
     */
    public static <T> void writeExcel(HttpServletResponse response, String fileName, Class<T> clazz, List<T> data) 
    public static <T> void writeExcel(HttpServletResponse response, String fileName, List<String> head, List<Map<String, Object>> data)
    /**
     * http excel写入
     *
     * @param response http响应对象
     * @param fileName 下载文件名称
     * @param clazz    easy报表对象
     * @param callback 数据写入操作
     */
    public static <T> void writeExcel(HttpServletResponse response, String fileName, Class<T> clazz, EasyListener.WriteCallback callback)
    public static OutputStream getOutputStream(String fileName, HttpServletResponse response)
```
#### 示例代码
```java
@ApiOperation("文章情绪占比导出数据")
@PostMapping("/article/excel")
public Response queryArticleTypeExcel(@RequestBody BigPageQueryDTO query) {
    List<PercentTypePageVO> data = bigPageService.queryArticleTypePage(query).getRows();
    ExcelUtil.writeExcel(WebUtils.getResponse(), "文章情绪占比导出数据", PercentTypePageExcel.class,
            PaasUtils.copyListTo(data, PercentTypePageExcel::new));
    return null;
}


@ApiOperation("近期趋势导出数据")
@PostMapping("/trend/excel")
public Response queryTrendExcel(@RequestBody BigPageQueryDTO query) {
    List<Map<String, Object>> data = bigPageService.queryTrendPage(query).getRows();
    List<String> title = findByMediaType(query.getLimit()).getData();
    title.add(0, "日期");
    ExcelUtil.writeExcel(WebUtils.getResponse(), "近期趋势导出数据", title, data);
    return null;
}


@ApiOperation("文章情绪占比导出数据")
@PostMapping("/article/excel")
public void queryArticleTypeExcel(@RequestBody BigPageQueryDTO query) throws IOException {
    if (query.getPage() != null) {
        query.getPage().setSize(10000L);
    }
    Response responseFile = bigDownFacade.queryArticleTypeExcel(query);
    if (responseFile == null) {
        return;
    }
    InputStream inputStream = responseFile.body().asInputStream();
    HttpServletResponse response = WebUtils.getResponse();
    ExcelUtil.getOutputStream("文章情绪占比导出数据", response);
    IOUtils.copy(inputStream, response.getOutputStream());
    response.flushBuffer();
}
```
### OkHttpUtil
提供http请求常用静态方法
```java

    /**
     * POST方式提交xml格式数据
     *
     * @param url    访问地址
     * @param xmlStr 数据
     * @return body string字符串，失败返回""
     */
    public static String sendPostXml(String url, String xmlStr) 

    /**
     * POST方式提交表单数据
     *
     * @param url    访问地址
     * @param params 数据
     * @return body string字符串，失败返回""
     */
    public static String sendPostForm(String url, Map<String, String> params)


    /**
     * json数据POST请求
     *
     * @param url     请求地址
     * @param jsonStr 字符串
     * @return
     */
    public static String sendPost(String url, String jsonStr) 
        
    /**
     * 自定义类型POST请求
     *
     * @param url         请求地址
     * @param data        数据
     * @param contentType 数据类型
     * @return
     */
    public static String sendPost(String url, String data, String contentType) 
        
    /**
     * json数据POST请求-可设置header
     *
     * @param url       请求地址
     * @param jsonStr   数据
     * @param headerMap 请求header参数
     * @return
     */
    public static String sendPost(String url, String jsonStr, Map<String, String> headerMap) 
    /**
     * 自定义类型POST请求-可设置header
     *
     * @param url         请求地址
     * @param data        数据
     * @param headerMap   请求header参数
     * @param contentType 数据类型
     * @return
     */
    public static String sendPost(String url, String data, Map<String, String> headerMap, String contentType) 

    /**
     * json数据PUT请求
     *
     * @param url     请求地址
     * @param jsonStr 字符串
     * @return
     */
    public static String sendPut(String url, String jsonStr) 
    /**
     * 自定义类型PUT请求
     *
     * @param url         请求地址
     * @param data        数据
     * @param contentType 数据类型
     * @return
     */
    public static String sendPut(String url, String data, String contentType)
    /**
     * json数据put请求-可设置header
     *
     * @param url       请求地址
     * @param jsonStr   数据
     * @param headerMap 请求header参数
     * @return
     */
    public static String sendPut(String url, String jsonStr, Map<String, String> headerMap) 
    /**
     * 自定义类型PUT请求-可设置header
     *
     * @param url         请求地址
     * @param data        数据
     * @param headerMap   请求header参数
     * @param contentType 数据类型
     * @return
     */
    public static String sendPut(String url, String data, Map<String, String> headerMap, String contentType) 
```
#### 代码示例
```java
@Override
@RedisCache(expireTime = 1, timeUnit = TimeUnit.HOURS)
public List<EventHandleTypeVO> getEventHandleType(String url) {
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("Authorization", Authorization);
    String resultStr = OkHttpUtil.sendGet(url, headerMap);
    Map result = JSON.parseObject(resultStr, Map.class);
    Object code = result.get("code");
    if (code != null && Integer.valueOf(code.toString()) == 200) {
        List<EventHandleTypeVO> ret = JSON.parseArray(JSON.toJSONString(result.get("results")), EventHandleTypeVO.class);
        return ret;
    } else {
        return null;
    }
}


Map<String, String> params = new HashMap<>();
params.put("ssd", DateUtils.formatDateTime(startDate, DateTimeEnum.Y_M_D_H_M_S));
params.put("sed", DateUtils.formatDateTime(endDate, DateTimeEnum.Y_M_D_H_M_S));
params.put("psd", DateUtils.formatDateTime(DateUtils.dateMinus(startDate, RedisKeyConstants.REDIS_DATA_EXPIRE_DAY, TimeUnit.DAYS), DateTimeEnum.Y_M_D_H_M_S));
XiYingVo result = JSON.parseObject(OkHttpUtil.sendPostForm(channel.getUrl(), params), XiYingVo.class);

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

# 其它工具

## **代码生成工具**

1. 代码自动按表自动生成数据库 Entity 对象，DTO 请求参数对象，QueryDTO 分页参数对象，VO 响应参数对象。
2. Controller: 按 ID 查询详情，新增，修改，删除，表对象分页查询，分页下拉选择接口
3. Service: CRUD+批量查询+下拉选择业务代码。
4. Mapper: 分页查询 SQL 方法，分页下拉选择 SQL 方法，批量插入 SQL 方法。Mapper 自带判断是否存在指定数据（exists）数据 ID 获取中文名称（convertToName）批量插入基础方法。

平台地址：http://127.0.0.1:9191/
账号密码：admin/admin

### 同步数据库信息

![image.png](./img/1.png)

### 下载代码

![image.png](./img/2.png)

### 新增模块

![image.png](./img/3.png)
复制文件到指定目录后，打开文件格式化代码，并完成自动导包过程。
![image.png](img/4.png)
修改 Controller url 地址，建议去表前线，单词以“/”分划
![image.png](./img/5.png)
修改 Mapper.xml 路径，
![image.png](img/6.png)
修改模块下拉接口响应值和名称对应字段。原则上一张表中数据只对外提供一组 value,label 值。防止业务数据绑定错乱。
![image.png](./img/7.png)
Feign 选择性导入。不对外提供接口的模块可不导入，导入后修改服务名与 path 服务提供方路径。并让服务提供方实现该接口。方便代码查看
![image.png](./img/8.png)
![image.png](./img/9.png)

### 模块变更

配置下载代码目录和应用所在目录。执行 EXE 文件
![image.png](./img/10.png)
![image.png](./img/11.png)
![image.png](./img/12.png)
模块对应 Mapper.xml 自行复制新增字段。

## **批量推送工具**

命令行 mvn 可正常使用。配置好项目工具目录，执行 exe 程序。
![image.png](./img/13.png)
