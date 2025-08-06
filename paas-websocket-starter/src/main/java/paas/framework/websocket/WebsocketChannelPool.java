package paas.framework.websocket;

import paas.framework.websocket.dto.Header;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;

public class WebsocketChannelPool {

    public WebsocketChannelPool() {
    }

    //public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    public static ConcurrentHashMap<Channel, Header> channelGroup = new ConcurrentHashMap();

}
