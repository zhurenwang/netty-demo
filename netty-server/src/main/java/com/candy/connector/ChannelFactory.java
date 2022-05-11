package com.candy.connector;

import com.candy.handler.TcpTransportChannelHandler;
import com.candy.protocol.CandyLengthFieldBasedFrameDecoder;
import com.candy.protocol.MessageCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 65157
 */
@Data
@Slf4j
public class ChannelFactory {

    private static NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private static Bootstrap b;
    static {
        b = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new TcpTransportChannelHandler());
                    }
                });
    }
    private ChannelFactory() {
    }

    public static Promise<Channel> getChannel(String host, int port) {
        Promise<Channel> promise = new DefaultPromise<>(eventLoopGroup.next());
        b.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                Channel channel = ((ChannelFuture) future).channel();
                log.info("远程连接成功：remoteAddr:{}", channel.remoteAddress().toString());
                promise.setSuccess(channel);
            }else {
                promise.setFailure(future.cause());
            }
        });
        return promise;
    }
}
