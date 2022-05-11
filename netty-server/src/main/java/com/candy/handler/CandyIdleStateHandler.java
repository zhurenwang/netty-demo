package com.candy.handler;

import com.candy.pool.TunnelChannelPool;
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
                case READER_IDLE:
                    ctx.channel().close();
                    TunnelChannelPool.remove(ctx.channel().id().asLongText());
                default:
            }
        }
    }
}
