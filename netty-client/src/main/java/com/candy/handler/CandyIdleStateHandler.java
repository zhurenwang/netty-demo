package com.candy.handler;

import com.candy.message.PingMessage;
import com.candy.pool.ChannelPool;
import com.candy.tunnel.TunnelClient;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CandyIdleStateHandler extends ChannelDuplexHandler {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            switch (((IdleStateEvent) evt).state()) {
                case WRITER_IDLE:
                    PingMessage pingMessage = new PingMessage();
                    ctx.channel().writeAndFlush(pingMessage);
                    break;
                case READER_IDLE:
                    //ChannelPool.clear();
                    TunnelClient.reConnect();
                    break;
                default:
            }
        }
    }
}
