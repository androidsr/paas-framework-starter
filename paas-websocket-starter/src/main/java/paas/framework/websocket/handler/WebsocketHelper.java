package paas.framework.websocket.handler;


import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import paas.framework.tools.JSON;
import paas.framework.websocket.WebsocketChannelPool;
import paas.framework.websocket.dto.Header;
import paas.framework.websocket.dto.WebsocketModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Component("websocketSendHelper")
public class WebsocketHelper {
    /**
     * 所有连接推送消息
     *
     * @return
     */
    public Collection<Header> getOnline() {
        return WebsocketChannelPool.channelGroup.values();
    }

    /**
     * 向所有平台发送消息
     *
     * @param request 发送数据
     * @return
     */
    public boolean sendToAllPlatform(WebsocketModel request) {
        for (Channel channel : WebsocketChannelPool.channelGroup.keySet()) {
            if (channel.isActive()) {
                channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(request)));
            } else {
                WebsocketChannelPool.channelGroup.remove(channel);
            }
        }
        return true;
    }

    /**
     * 指定appId按所有连接推送消息
     *
     * @param appId   应用标识
     * @param request 发送数据
     * @return
     */
    public boolean sendToAppId(String appId, WebsocketModel request) {
        for (Channel channel : WebsocketChannelPool.channelGroup.keySet()) {
            Header header = WebsocketChannelPool.channelGroup.get(channel);
            if (appId.equals(header.getAppId())) {
                if (channel.isActive()) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(request)));
                } else {
                    WebsocketChannelPool.channelGroup.remove(channel);
                }
            }
        }
        return true;
    }

    /**
     * 用户推送消息
     *
     * @param appId   应用标识
     * @param userId  用户标识
     * @param request 发送数据
     * @return
     */
    public boolean sendToUserId(String appId, String userId, WebsocketModel request) {
        for (Channel channel : WebsocketChannelPool.channelGroup.keySet()) {
            Header header = WebsocketChannelPool.channelGroup.get(channel);
            if (appId.equals(header.getAppId()) && userId.equals(header.getUserId())) {
                if (channel.isActive()) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(request)));
                } else {
                    WebsocketChannelPool.channelGroup.remove(channel);
                }
            }
        }
        return true;
    }

    /**
     * 多用户发送消息
     *
     * @param appId   应用标识
     * @param userIds 用户标识
     * @param request 发送数据
     * @return 失败用户标识
     */
    public List<String> sendToUserIds(String appId, List<String> userIds, WebsocketModel request) {
        List<String> failUserIds = new ArrayList<>();
        for (Channel channel : WebsocketChannelPool.channelGroup.keySet()) {
            Header header = WebsocketChannelPool.channelGroup.get(channel);
            if (appId.equals(header.getAppId())) {
                for (String user : userIds) {
                    if (!header.getUserId().equals(user)) {
                        continue;
                    }
                    if (channel.isActive()) {
                        channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(request)));
                    } else {
                        failUserIds.add(user);
                        WebsocketChannelPool.channelGroup.remove(channel);
                    }
                }
            }
        }
        return failUserIds;
    }

    /**
     * 客户端类型发送消息
     *
     * @param appId      应用标识
     * @param clientType [web,app,小程序,公众号]
     * @param request    发送数据
     * @return
     */
    public boolean sendToClientType(String appId, String clientType, WebsocketModel request) {
        for (Channel channel : WebsocketChannelPool.channelGroup.keySet()) {
            Header header = WebsocketChannelPool.channelGroup.get(channel);
            if (appId.equals(header.getAppId()) && clientType.equals(header.getClientType())) {
                if (channel.isActive()) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(request)));
                } else {
                    WebsocketChannelPool.channelGroup.remove(channel);
                }
            }
        }
        return true;
    }

    /**
     * 平台类型发送消息
     *
     * @param appId   应用标识
     * @param request 发送数据
     * @return
     */
    public boolean sendToPlatformType(String appId, String platformType, WebsocketModel request) {
        for (Channel channel : WebsocketChannelPool.channelGroup.keySet()) {
            Header header = WebsocketChannelPool.channelGroup.get(channel);
            if (appId.equals(header.getAppId()) && platformType.equals(header.getPlatformType())) {
                if (channel.isActive()) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(request)));
                } else {
                    WebsocketChannelPool.channelGroup.remove(channel);
                }
            }
        }
        return true;
    }

    /**
     * 分组类型发送消息
     *
     * @param appId   应用标识
     * @param groupId 分组标识
     * @param request 发送数据
     * @return
     */
    public boolean sendToGroupId(String appId, String groupId, WebsocketModel request) {
        for (Channel channel : WebsocketChannelPool.channelGroup.keySet()) {
            Header header = WebsocketChannelPool.channelGroup.get(channel);
            if (appId.equals(header.getAppId()) && groupId.equals(header.getGroupId())) {
                if (channel.isActive()) {
                    channel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(request)));
                } else {
                    WebsocketChannelPool.channelGroup.remove(channel);
                }
            }
        }
        return true;
    }
}
