# 接口文档说明
服务接口集成可通过swagger和smart-doc两种方案。swagger参考swagger使用文档。
smart-doc默认paas基础组件包已默认集成，项目需要在自己的resources目录下增加smart-doc.json即可。
## smart-doc集成（推荐）
paas基于组件默认指定配置上当为：${basedir}/src/main/resources/smart-doc.json
```json
{
  "outPath": "target/doc",
  "projectName": "微服务demo",
  "packageFilters": "paas.cloud.app.controller.*",
  "openUrl": "http://127.0.0.1:7700/api",
  "appToken": "23adce9c69214684ad68dd256dfe350d",
  "debugEnvName":"本地环境",
  "debugEnvUrl":"http://127.0.0.1:8080",
  "isReplace": true,
  "requestExample": false,
  "responseExample": false,
  "dataDictionaries": [
    {
      "title": "客户端类型",
      "enumClassName": "paas.user.center.db.enums.ClientTypeEnum",
      "codeField": "code",
      "descField": "title"
    }
  ],
  "requestHeaders": [
    {
      "name": "Authorization",
      "type": "string",
      "desc": "认证TOKEN",
      "required": false,
      "value": "",
      "since": "-"
    }
  ],
  "errorCodeDictionaries": [
    {
      "enumClassName": "paas.model.enums.ResultMessage",
      "codeField": "code",
      "descField": "message"
    }
  ]
}
```
根据项目情况进行修改配置文件内容：

| 配置项 | 说明 |
| --- | --- |
| openUrl | 文档平台服务器地址 |
| appToken | 文档平台创建项目模块下的token信息 |
| projectName | 服务项目名称 |
| errorCodeDictionaries | 定义的错误信息 |

## 代码编写要求
smart-doc基于注释进行生成接口文档，因此项目编码时需要对注释按java规范进行编写。
所有注释采用标识多行注释要求，需包含类注释，类作者，方法注释，方法参数注释，对象属性注释等。
## 文档推送
文档推送在项目下通过maven插件找到smart-doc插件下smart-doc:torna-rest选项执行即可。
项目在进行构建时应该同步推送接口文档至文档服务器。


# Swagger文档
swagger集成配置，针对web兼容性进行了处理，并对enum参数进行了特殊处理
## maven依赖
```xml
<dependency>
	<groupId>paas.framework.starter</groupId>
	<artifactId>paas-swagger-starter</artifactId>
</dependency>
```
## yml配置
```yaml
swagger:
  enable: true
  appName: "XXX服务"
  appVersion: "v1.0"
  appDesc: "XXX服务"
  tryHost: http://localhost:9098
  contactName: "联系人"
  contactEmail: "联系人邮箱"
```

## 示例代码
```java

/**
 * 字典管理
 *
 * @author sirui
 */
@Api(tags = "字典管理")
@Slf4j
@RestController
@RequestMapping("/common/dict")
public class CommonDictController implements CommonDictFacade {

    @Autowired
    CommonDictService commonDictService;

    /**
     * 分页下拉选择数据
     *
     * @param dto 查询参数
     * @return
     */
    @ApiOperation("分页下拉选择数据")
    @Override
    @RedisCache(key = "#dto", expireTime = CacheTimeoutConstants.MINUTE_5)
    public HttpResult<List<CommonDictVO>> findByGroupId(@RequestBody CommonDictQueryDTO dto) {
        return HttpResult.ok(commonDictService.findByGroupId(dto));
    }
}

```

```java
@ApiModel("数据字典")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CommonDictVO implements Serializable {
    /**
     * 主键
     */
    @ApiModelProperty("主键")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    /**
     * 分组编号
     */
    @ApiModelProperty("分组编号")
    private String groupId;
}
```
