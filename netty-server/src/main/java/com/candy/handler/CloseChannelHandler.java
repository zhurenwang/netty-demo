package com.candy.handler;

import com.candy.message.CloseChannelRequestMessage;
import com.candy.message.PingMessage;
import com.candy.message.PongMessage;
import com.candy.pool.ChannelPool;
import com.candy.pool.TunnelChannelPool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class CloseChannelHandler extends SimpleChannelInboundHandler<CloseChannelRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloseChannelRequestMessage msg) throws Exception {
        String connId = msg.getConnId();
        String tunnelConnId = ctx.channel().id().asLongText();
        String connAt = tunnelConnId + "@" + connId;
        Channel channel = ChannelPool.getChannel(connAt);
        if (Objects.nonNull(channel)) {
            channel.close();
            ChannelPool.remove(channel);
            log.info("远程关闭成功: {}", channel.remoteAddress());
        }
    }
}
