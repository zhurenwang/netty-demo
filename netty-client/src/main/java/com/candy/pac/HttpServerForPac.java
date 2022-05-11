package com.candy.pac;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

public class HttpServerForPac {

    private NioEventLoopGroup eventLoopGroup;

    public HttpServerForPac (NioEventLoopGroup eventLoopGroup) {
        this.eventLoopGroup = eventLoopGroup;
    }
    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new HttpForPacHandler());
                    }
                });
        bootstrap.bind(1080);
    }
}
