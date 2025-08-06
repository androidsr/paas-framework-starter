package paas.framework.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Component
public class WebsocketControl {

    @Autowired
    private ServerBootstrap serverBootstrap;

    @Autowired
    NioEventLoopGroup bossGroup;

    @Autowired
    NioEventLoopGroup workerGroup;

    private Channel channel;

    public void start(int port) {
        try {
            channel = serverBootstrap.bind(port).sync().channel().closeFuture().channel();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PreDestroy
    public void close() {
        try {
            bossGroup.shutdownGracefully();
        } catch (Exception e) {
        }
        try {
            workerGroup.shutdownGracefully();
        } catch (Exception e) {
        }
        try {
            channel.close();
        } catch (Exception e) {
        }
        try {
            channel.parent().close();
        } catch (Exception e) {
        }
    }
}
