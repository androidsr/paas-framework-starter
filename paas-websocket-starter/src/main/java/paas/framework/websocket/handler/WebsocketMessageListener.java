package paas.framework.websocket.handler;

import io.netty.channel.ChannelHandlerContext;
import paas.framework.model.exception.BusException;
import paas.framework.websocket.dto.WebsocketModel;

public abstract class WebsocketMessageListener {

    protected boolean bind(ChannelHandlerContext ctx, WebsocketModel model) {
        return true;
    }

    protected void beatHandler(ChannelHandlerContext ctx, Object evt) {

    }

    protected abstract void reader(WebsocketModel model) throws BusException;

}
