package com.candy.handler;

import com.candy.message.ConnectRequestMessage;
import com.candy.pool.ChannelPool;
import com.candy.tunnel.TunnelClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class HttpRequestDecoderHandler extends SimpleChannelInboundHandler<HttpRequest> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
        if (Objects.equals(msg.method().name(), "CONNECT")) {
            log.info("接收到https代理请求: {}, method: {}", msg.uri(), msg.method().name());
            //String uuid = UUID.randomUUID().toString().replace("-", "");
            String uuid = ctx.channel().id().asLongText();
            String[] split = msg.uri().split(":");
            ConnectRequestMessage crm = new ConnectRequestMessage();
            crm.setHost(split[0]);
            crm.setPort(Integer.parseInt(split[1]));
            crm.setConnId(uuid);
            TunnelClient.getChannel().writeAndFlush(crm);
            ChannelPool.setChannel(uuid, ctx.channel());
            ctx.pipeline().remove("HttpRequestDecoder");
            ctx.pipeline().remove("HttpRequestDecoderHandler");
            //ctx.pipeline().remove("HttpContentDecoderHandler");
        }else {
            String uri = msg.uri();
            String replaceUrl = uri.replace("http", "https");
            String header = "Location: " + replaceUrl+"\r\n\r\n";
            //如果是http协议重定向到https协议  不直接代理http请求
            ByteBuf response = ctx.alloc().buffer();
            response.writeBytes("HTTP/1.1 302\r\n".getBytes(StandardCharsets.UTF_8));
            response.writeBytes(header.getBytes(StandardCharsets.UTF_8));
            ctx.writeAndFlush(response);
            log.info("接收到http代理请求 已重定向到https: {}, method: {}", replaceUrl, msg.method().name());
        }
    }
}
