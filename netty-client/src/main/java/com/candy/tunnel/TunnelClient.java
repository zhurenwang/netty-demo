package com.candy.tunnel;

import com.candy.handler.CandyChannelInitializer;
import com.candy.pool.ChannelPool;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultProgressivePromise;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @author 65157
 */
@Data
@Slf4j
public class TunnelClient {

    private static volatile Channel channel;

    private static Bootstrap b;
    private static ScheduledFuture<?> scheduledFuture;
    private static String host;
    private static int port;
    private static NioEventLoopGroup eventLoopGroup;

    public static void connect(String host, int port, NioEventLoopGroup eventLoopGroup) {
        try {
            TunnelClient.host = host;
            TunnelClient.port = port;
            TunnelClient.eventLoopGroup = eventLoopGroup;
            b = new Bootstrap()
                    .group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .handler(new CandyChannelInitializer());
            ChannelFuture channelFuture = b.connect(host, port).sync();
            channel = channelFuture.channel();
            channelFuture.channel().closeFuture().addListener(future -> {
                log.info("远程服务器已关闭！！！！！！！");
                ChannelPool.clear();
            });
            eventLoopGroup.scheduleAtFixedRate(TunnelClient::reConnect, 0, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("e", e);
            throw new RuntimeException("创建远程服务连接失败", e);
        }
    }

    public static void reConnect() {
        try {
            if (channel.isOpen()) {
                return;
            }
            log.info("正在重连..........");
            ChannelFuture channelFuture = b.connect(host, port).sync();
            if (channelFuture.channel().isOpen()) {
                channel = channelFuture.channel();
                log.info("重连成功..........");
            }
        } catch (Exception e) {
            log.error("连接远程失败 :{}", e.getMessage());
        }
    }

    public static Channel getChannel() {
        return channel;
    }

}
