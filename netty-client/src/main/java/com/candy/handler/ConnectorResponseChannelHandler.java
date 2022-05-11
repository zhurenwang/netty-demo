package com.candy.handler;

import com.candy.message.ConnectResponseMessage;
import com.candy.pool.ChannelPool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class ConnectorResponseChannelHandler extends SimpleChannelInboundHandler<ConnectResponseMessage> {

    private static final String REPTY = "HTTP/1.1 200 Connection Established\r\n\r\n";

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnectResponseMessage msg) throws Exception {
        log.info("接收到代理响应: {}, status: {}", msg.getHost(), msg.isSuccess()?"success":"fail");
        Channel channel = ChannelPool.getChannel(msg.getConnId());
        if (Objects.isNull(channel)) {
            return;
        }
        if (msg.isSuccess()) {
            ByteBuf byteBuf = ctx.alloc().heapBuffer();
            byteBuf.writeBytes(REPTY.getBytes(StandardCharsets.UTF_8));
            channel.writeAndFlush(byteBuf);
        }else {
            channel.close();
            ChannelPool.remove(msg.getConnId());
        }
    }
}
