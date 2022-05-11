package com.candy.handler;

import com.candy.message.ConnectRequestMessage;
import com.candy.pool.ChannelPool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class HttpContentDecoderHandler extends SimpleChannelInboundHandler<HttpContent> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpContent msg) throws Exception {

    }
}
