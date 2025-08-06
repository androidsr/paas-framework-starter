package paas.framework.jpush;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.ServiceHelper;
import cn.jiguang.common.connection.NettyHttpClient;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import io.netty.handler.codec.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * 极光消息推送
 */
@Slf4j
@Component
public class JPushHelper {

    @Autowired
    JpushProperties jpushProperties;

    /**
     * 发送自定义消息，由APP端拦截信息后再决定是否创建通知
     *
     * @param title     App通知栏标题
     * @param content   App通知栏内容（为了单行显示全，尽量保持在22个汉字以下）
     * @param extrasMap 额外推送信息（不会显示在通知栏，传递数据用）
     * @param audiences 别名数组，设定哪些用户手机能接收信息（为空则所有用户都推送）
     * @return PushResult
     */
    public PushResult sendCustomPush(String title, String content, Map<String, String> extrasMap, SendTypeEnum sendType, String... audiences) throws APIConnectionException, APIRequestException {
        ClientConfig clientConfig = ClientConfig.getInstance();
        clientConfig.setTimeToLive(jpushProperties.getLiveTime());
        clientConfig.setApnsProduction(jpushProperties.getApnsProduction());
        JPushClient jpushClient = new JPushClient(jpushProperties.getMasterSecret(), jpushProperties.getAppKey(), null, clientConfig);
        PushPayload payload = buildCustomPushPayload(title, content, extrasMap, sendType, audiences);
        PushResult result = jpushClient.sendPush(payload);
        return result;
    }

    /**
     * 发送通知消息
     *
     * @param title     App通知栏标题
     * @param content   App通知栏内容（为了单行显示全，尽量保持在22个汉字以下）
     * @param extrasMap 额外推送信息（不会显示在通知栏，传递数据用）
     * @param tags      标签数组，设定哪些用户手机能接收信息（为空则所有用户都推送）
     */
    public PushResult sendPush(String title, String content, Map<String, String> extrasMap, SendTypeEnum sendType, String... audiences) throws APIConnectionException, APIRequestException {
        ClientConfig clientConfig = ClientConfig.getInstance();
        clientConfig.setTimeToLive(Long.valueOf(jpushProperties.getLiveTime()));
        clientConfig.setApnsProduction(jpushProperties.getApnsProduction());
        // 使用NativeHttpClient网络客户端，连接网络的方式，不提供回调函数
        JPushClient jpushClient = new JPushClient(jpushProperties.getMasterSecret(), jpushProperties.getAppKey(), null, clientConfig);
        // 设置推送方式
        PushPayload payload = buildPushLoad(title, content, extrasMap, sendType, audiences);
        PushResult result = jpushClient.sendPush(payload);
        return result;
    }


    /**
     * 异步请求推送方式
     * 使用NettyHttpClient,异步接口发送请求，通过回调函数可以获取推送成功与否情况
     *
     * @param title     通知栏标题
     * @param content   通知栏内容（为了单行显示全，尽量保持在22个汉字以下）
     * @param extrasMap 额外推送信息（不会显示在通知栏，传递数据用）
     * @param alias     需接收的用户别名数组（为空则所有用户都推送）
     */
    public void sendPushWithCallback(String title, String content, Map<String, String> extrasMap, NettyHttpClient.BaseCallback callback, SendTypeEnum sendType, String... audiences) {
        ClientConfig clientConfig = ClientConfig.getInstance();
        clientConfig.setTimeToLive(Long.valueOf(jpushProperties.getLiveTime()));
        String host = (String) clientConfig.get(ClientConfig.PUSH_HOST_NAME);
        NettyHttpClient client = new NettyHttpClient(ServiceHelper.getBasicAuthorization(jpushProperties.getAppKey(), jpushProperties.getMasterSecret()), null, clientConfig);
        try {
            URI uri = new URI(host + clientConfig.get(ClientConfig.PUSH_PATH));
            PushPayload payload = buildPushLoad(title, content, extrasMap, sendType, audiences);
            client.sendRequest(HttpMethod.POST, payload.toString(), uri, callback);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    /**
     * 设置、更新、设备的 tag, alias 信息。
     *
     * @param registrationId 设备的registrationId
     * @param alias          更新设备的别名属性
     * @param tagsToAdd      添加设备的tag属性
     * @param tagsToRemove   移除设备的tag属性
     */
    public void updateDeviceTagAlias(String registrationId, String alias, Set<String> tagsToAdd, Set<String> tagsToRemove) throws APIConnectionException, APIRequestException {
        JPushClient jpushClient = new JPushClient(jpushProperties.getMasterSecret(), jpushProperties.getAppKey());
        jpushClient.updateDeviceTagAlias(registrationId, alias, tagsToAdd, tagsToRemove);
    }

    /**
     * 根据标签推送相应的消息
     *
     * @param title     推送消息标题
     * @param content   推送消息内容
     * @param extrasMap 推送额外信息
     * @param tags      推送的目标标签
     * @return
     */
    private PushPayload buildPushLoad(String title, String content, Map<String, String> extrasMap, SendTypeEnum sendType,
                                      String... audiences) {
        if (extrasMap.isEmpty()) {
            extrasMap = new HashMap<>();
        }
        //批量删除数组中的空元素
        String[] newTags = removeArrayEmptyElement(audiences);
        Audience audience;
        if (sendType == SendTypeEnum.TAGS) {
            audience = Audience.tag(newTags);
        } else {
            audience = Audience.alias(newTags);
        }
        return PushPayload.newBuilder()
                //设置推送平台为安卓
                .setPlatform(Platform.all())
                //设置标签
                .setAudience(audience)
                //设置 推送的标签标题
                // 设置通知方式(以alert方式提醒)
                .setNotification(Notification.newBuilder()
                        .setAlert(content)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(title)
                                .addExtras(extrasMap)
                                .build())
                        .build())
                //设置通知内容
                .setMessage(Message.newBuilder().setTitle(title).setMsgContent(content).addExtras(extrasMap).build())
                .build();
    }

    /**
     * 构建Android和IOS的自定义消息的推送消息对象
     *
     * @return PushPayload
     */
    private PushPayload buildCustomPushPayload(String title, String content, Map<String, String> extrasMap,
                                               SendTypeEnum sendType, String... audiences) {
        // 批量删除数组中空元素
        String[] newAlias = removeArrayEmptyElement(audiences);
        Audience audience;
        if (sendType == SendTypeEnum.TAGS) {
            audience = Audience.tag(newAlias);
        } else {
            audience = Audience.alias(newAlias);
        }
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience((null == newAlias || newAlias.length == 0) ? Audience.all() : audience)
                .setMessage(Message.newBuilder()
                        .setTitle(title)
                        .setMsgContent(content)
                        .addExtras(extrasMap)
                        .build())
                .build();
    }

    /**
     * 删除别名中的空元素（需删除如：null,""," "）
     *
     * @param strArray
     * @return String[]
     */
    private String[] removeArrayEmptyElement(String... strArray) {
        if (null == strArray || strArray.length == 0) {
            return null;
        }
        List<String> tempList = Arrays.asList(strArray);
        List<String> strList = new ArrayList<String>();
        Iterator<String> iterator = tempList.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            // 消除空格后再做比较
            if (null != str && !"".equals(str.trim())) {
                strList.add(str);
            }
        }
        // 若仅输入"",则会将数组长度置为0
        String[] newStrArray = strList.toArray(new String[strList.size()]);
        return newStrArray;
    }
}
