package paas.framework.websocket.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import paas.framework.model.exception.BusException;
import paas.framework.model.web.HttpResult;
import paas.framework.tools.JSON;
import paas.framework.websocket.WebsocketChannelPool;
import paas.framework.websocket.dto.Header;
import paas.framework.websocket.dto.WebsocketModel;
import paas.framework.websocket.enums.WsActionEnum;

import java.nio.charset.Charset;

@Slf4j
@Component
@ChannelHandler.Sharable
public class WebsocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    @Resource
    WebsocketMessageListener websocketMessageListener;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        try {
            if (frame instanceof CloseWebSocketFrame) {
                ctx.channel().close();
                log.error("关闭请求...");
                return;
            }
            if (frame instanceof PingWebSocketFrame) {
                PingWebSocketFrame pingFrame = (PingWebSocketFrame) frame;
                ctx.write(new PongWebSocketFrame(pingFrame.content().retain()));
                return;
            }

            String content = frame.content().toString(Charset.defaultCharset());
            log.debug("websocket消息：{}", content);
            if ("9".equals(content) || "ping".equals(content)) {
                ctx.channel().writeAndFlush(new TextWebSocketFrame(content));
                return;
            }
            WebsocketModel request;
            try {
                request = JSON.parseObject(content, WebsocketModel.class);
            } catch (Exception e) {
                log.error("websocket请求数据格式不正确：{}", e.getMessage());
                ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(HttpResult.fail("请求数据格式不正确"))));
                return;
            }
            if (request.getHeader() != null && WsActionEnum.BIND.name().equals(request.getAction())) {
                if (websocketMessageListener.bind(ctx, request)) {
                    if (ObjectUtils.isEmpty(request.getHeader())) {
                        ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(HttpResult.fail("请求数据格式不正确"))));
                        return;
                    }
                    WebsocketChannelPool.channelGroup.put(ctx.channel(), request.getHeader());
                    log.debug("绑定成功：{}", request);
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(HttpResult.ok())));
                }
            } else {
                try {
                    Header header = WebsocketChannelPool.channelGroup.get(ctx.channel());
                    request.setHeader(header);
                    websocketMessageListener.reader(request);
                } catch (BusException e) {
                    e.printStackTrace();
                    log.error("websocket消息处理失败：{}", e.getMessage());
                    ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(HttpResult.fail(e.getMessage()))));
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("websocket消息处理异常：{}", e.getMessage());
            ctx.channel().writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(HttpResult.fail(e.getMessage()))));
            return;
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        WebsocketChannelPool.channelGroup.put(ctx.channel(), new Header());
        log.debug("加入了连接");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        WebsocketChannelPool.channelGroup.remove(ctx.channel());
        log.debug("退出了连接：{}");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        log.error(channel.remoteAddress() + " 连接异常,断开连接...");
        cause.printStackTrace();
        ctx.channel().close();
        WebsocketChannelPool.channelGroup.remove(channel);
    }
}
