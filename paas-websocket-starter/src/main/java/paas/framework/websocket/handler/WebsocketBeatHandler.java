package paas.framework.websocket.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

@Slf4j
@ChannelHandler.Sharable
@Component
public class WebsocketBeatHandler extends ChannelInboundHandlerAdapter {
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    @Resource
    WebsocketMessageListener websocketMessageListener;

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case READER_IDLE:
                    try {
                        Integer count = threadLocal.get();
                        log.debug("心跳超时...{}", count);
                        if (count == null) {
                            count = 0;
                        } else if (count > 3) {
                            log.debug("客户端状态：{}", ctx.channel().isActive());
                            if (!ctx.channel().isActive()) {
                                return;
                            }
                            try {
                                threadLocal.remove();
                            } catch (Exception e) {
                            }
                            ctx.channel().close();
                        }
                        threadLocal.set(count + 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error("心跳下线客户端异常：{}", e.getMessage());
                    }
                    return;
            }
        }
        websocketMessageListener.beatHandler(ctx, evt);
    }
}
