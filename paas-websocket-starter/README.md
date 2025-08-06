# websocket消息
提供基于netty提供websocket服务，提供点对点消息，系统消息，群发消息，指定组发送消息。分组类型按实际功能设计初始化规则自行定义。
## maven依赖
```xml
<dependency>
	<groupId>paas.framework.starter</groupId>
	<artifactId>paas-websocket-starter</artifactId>
</dependency>
```
## yml配置
```yaml
paas:
  websocket:
    port: 8888
```
## 启动初始化
```java

@Component
public class StartWebsocket {
    @Value("${paas.websocket.port}")
    int port;
    @Resource
    ConfigurableApplicationContext ctx;

    @PostConstruct
    public void start() {
        WebsocketControl control = ctx.getBean(WebsocketControl.class);
        control.start(port);
        System.out.println("websocket 启动成功");
    }
}
```
## 后端工具类
```java
/**
     * 所有连接推送消息
     *
     * @return
     */
    public Collection<Header> getOnline()

    /**
     * 向所有平台发送消息
     *
     * @param request 发送数据
     * @return
     */
    public boolean sendToAllPlatform(WebsocketModel request)

    /**
     * 指定appId按所有连接推送消息
     *
     * @param appId   应用标识
     * @param request 发送数据
     * @return
     */
    public boolean sendToAppId(String appId, WebsocketModel request)

    /**
     * 用户推送消息
     *
     * @param appId   应用标识
     * @param userId  用户标识
     * @param request 发送数据
     * @return
     */
    public boolean sendToUserId(String appId, String userId, WebsocketModel request)

    /**
     * 多用户发送消息
     *
     * @param appId   应用标识
     * @param userIds 用户标识
     * @param request 发送数据
     * @return 失败用户标识
     */
    public List<String> sendToUserIds(String appId, List<String> userIds, WebsocketModel request)
    /**
     * 客户端类型发送消息
     *
     * @param appId      应用标识
     * @param clientType [web,app,小程序,公众号]
     * @param request    发送数据
     * @return
     */
    public boolean sendToClientType(String appId, String clientType, WebsocketModel request) 
    /**
     * 平台类型发送消息
     *
     * @param appId   应用标识
     * @param request 发送数据
     * @return
     */
    public boolean sendToPlatformType(String appId, String platformType, WebsocketModel request) 

    /**
     * 分组类型发送消息
     *
     * @param appId   应用标识
     * @param groupId 分组标识
     * @param request 发送数据
     * @return
     */
    public boolean sendToGroupId(String appId, String groupId, WebsocketModel request) 
```

## 后端示例
通过请求报文类型action进行不同消息业务逻辑的判断处理；使期同一个消息服务支持不同类型的业务处理。
利用MQ消息队列发送消息处理业务逻辑，实现消息服务与业务服务分离；利用MQ消息广播方式处理消息群发处理，保证消息websocket服务集群群发消息逻辑。
```java

@Slf4j
@Service("websocketMessageListener")
public class WebsocketMessageImpl extends WebsocketMessageListener {

    @Autowired
    WebsocketHelper websocketHelper;

    @Override
    protected void reader(WebsocketModel model) {
        switch (model.getAction()) {
            case "SEND_VIDEO_CALL":
                sendVideoCallMsg(model);
                break;

            default:
                break;
        }

        log.info("在线人数 {}，接收到消息 ：{}", websocketHelper.getOnline().size(), model);
    }
    
	//接收消息后业务逻辑处理过程。
    private void sendVideoCallMsg(WebsocketModel model) {
        //获得消息体数据
        VideoCallMsgDTO data = JSON.parseObject(model.getData(), VideoCallMsgDTO.class);
        //组件发送消息对象
        WebsocketModel request = new WebsocketModel();
        //设置消息转发操作类型
        request.setAction("RECEIVE_VIDEO_CALL");
        //设置header用户标识（发起消息的用户header）
        request.setHeader(model.getHeader());
        //生成新的消息体
        Map<String, String> params = new HashMap<>();
        params.put("roomName", data.getRoomName());
        params.put("msg", data.getMsg());
        request.setData(JSON.toJSONString(params));
        //发送消息
        websocketHelper.sendToUserIds(model.getHeader().getAppId(), data.getReceiveUser(), request);
    }
}

```
## 前端示例
```jsx
let ws = new WebSocket(
  `http://localhost:8888/websocket`,
);

初始化绑定客户端信息
ws.onopen = () => {
    //初始化绑定操作
    let query = {
        appId: ,//应用标识发送消息时使用
        secret: ,//安全密钥-可进行验证绑定用户
        userId: ,//用户标识-全局用户唯一
        userName: ,//用户名
        clientType: "WEB",  //(web,app,小程序)实际业务约定
        platformType: "WEB", //（android,ios）实际业务约定
        groupId: "G1"  //实际业务约定
    }
    ws.send(JSON.stringify({ action: 'BIND', header: query }));
    //心跳检测
    setInterval(function () {
        ws.send(0x9, true);
    }, 10000)
};
//消费消息处理
ws.onmessage = evt => {
    let message = JSON.parse(evt.data);
    if (message == 'ping' || message == 9) return;
    //接收消息判断处理
    if (message.action == "RECEIVE_VIDEO_CALL") {
      let data = JSON.parse(message.data);
      //业务逻辑处理过程
    }
};
//发送消息
let sendData = {
  data: {},
  action: 'SEND_VIDEO_CALL',
};
ws.send(JSON.stringify(sendData));
//关闭连接
ws.onclose = () => {
    
};
```
