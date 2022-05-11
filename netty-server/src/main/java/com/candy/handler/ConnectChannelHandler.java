package com.candy.handler;

import com.candy.connector.ChannelFactory;
import com.candy.message.ConnectRequestMessage;
import com.candy.message.ConnectResponseMessage;
import com.candy.pool.ChannelPool;
import com.candy.pool.TunnelChannelPool;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class ConnectChannelHandler extends SimpleChannelInboundHandler<ConnectRequestMessage> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //String uuid = UUID.randomUUID().toString().replace("-", "");
        String uuid = ctx.channel().id().asLongText();
        TunnelChannelPool.setChannel(uuid, ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ConnectRequestMessage msg) throws Exception {
        Promise<Channel> promise = ChannelFactory.getChannel(msg.getHost(), msg.getPort());
        promise.addListener(future -> {
            ConnectResponseMessage responseMessage = new ConnectResponseMessage();
            responseMessage.setConnId(msg.getConnId());
            responseMessage.setHost(msg.getHost());
            responseMessage.setPort(msg.getPort());
            if (future.isSuccess()) {
                setChannelToPool(ctx.channel().id().asLongText(), msg.getConnId(), (Channel)future.get());
                responseMessage.setSuccess(true);
            } else {
                responseMessage.setSuccess(false);
            }
            ctx.writeAndFlush(responseMessage);
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        TunnelChannelPool.remove(ctx.channel().id().asLongText());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        TunnelChannelPool.remove(ctx.channel().id().asLongText());
        log.error("err", cause);
        super.exceptionCaught(ctx, cause);
    }

    private void setChannelToPool(String tunnelConnId, String connId, Channel channel) {
        ChannelPool.setChannel(tunnelConnId + "@" + connId, channel);
    }
}
