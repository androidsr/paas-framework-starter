package paas.framework.websocket;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import paas.framework.websocket.handler.WebsocketBeatHandler;
import paas.framework.websocket.handler.WebsocketHandler;

import java.util.concurrent.TimeUnit;

@AutoConfiguration
public class WebsocketServer {

    @Autowired
    WebsocketHandler websocketHandler;
    @Autowired
    WebsocketBeatHandler websocketBeatHandler;
    @Autowired
    PaasProperties paasProperties;

    @Bean
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup();
    }

    @Bean
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup();
    }

    @Bean
    public ServerBootstrap bootstrap() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup(), workerGroup())
                .option(ChannelOption.SO_BACKLOG, 1024)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) throws Exception {
                        channel.pipeline().addLast(new HttpServerCodec());
                        channel.pipeline().addLast(new ChunkedWriteHandler());
                        channel.pipeline().addLast(new IdleStateHandler(paasProperties.getBeatIdleTime(),0,  0, TimeUnit.SECONDS));
                        channel.pipeline().addLast(new HttpObjectAggregator(1024 * 1024 * 100));
                        channel.pipeline().addLast(new WebSocketServerProtocolHandler("/websocket", null, true));
                        channel.pipeline().addLast(websocketBeatHandler);
                        channel.pipeline().addLast(websocketHandler);
                    }
                }).option(ChannelOption.SO_BACKLOG, 100)
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        return serverBootstrap;
    }

}
