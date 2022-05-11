package com.candy;

import com.candy.handler.CandyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServerApplication {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new CandyChannelInitializer());
            ChannelFuture channelFuture = bootstrap.bind(444).sync();
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
            log.info("=================== NettyServerApplication启动成功 ==========================");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("e", e);
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
