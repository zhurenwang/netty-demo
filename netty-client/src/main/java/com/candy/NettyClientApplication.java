package com.candy;

import com.candy.handler.HttpContentDecoderHandler;
import com.candy.handler.HttpRequestDecoderHandler;
import com.candy.handler.TcpTransportChannelHandler;
import com.candy.pac.HttpServerForPac;
import com.candy.tunnel.TunnelClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClientApplication {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        //获取连接通道 proxy.candy-wind.com
        try {
            TunnelClient.connect("127.0.0.1", 444, boss);
            HttpServerForPac httpServerForPac = new HttpServerForPac(boss);
            httpServerForPac.start();
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boss)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast("HttpRequestDecoder", new HttpRequestDecoder());
                            ch.pipeline().addLast("HttpRequestDecoderHandler", new HttpRequestDecoderHandler());
                            ch.pipeline().addLast("HttpContentDecoderHandler", new HttpContentDecoderHandler());
                            ch.pipeline().addLast("TcpTransportChannelHandler", new TcpTransportChannelHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(1081).sync();
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
            log.info("=================== NettyClientApplication启动成功 ==========================");
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        } finally {
            boss.shutdownGracefully();
        }
    }

}
