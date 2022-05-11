package com.candy.handler;

import com.candy.message.CloseChannelRequestMessage;
import com.candy.message.CopyRequestMessage;
import com.candy.pool.ChannelPool;
import com.candy.pool.TunnelChannelPool;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.Objects;

@Slf4j
public class TcpTransportChannelHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("远程连接关闭: {}", ctx.channel().remoteAddress());
        CloseChannelRequestMessage closeMessage = new CloseChannelRequestMessage();
        String connId = ChannelPool.getConnId(ctx.channel());
        ChannelPool.remove(ctx.channel());
        if (StringUtil.isNullOrEmpty(connId)) {
            return;
        }
        String[] split = connId.split("@");
        closeMessage.setConnId(split[1]);
        TunnelChannelPool.getChannel(split[0]).writeAndFlush(closeMessage);
        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        CopyRequestMessage copyRequestMessage = new CopyRequestMessage();
        String connIdAt = ChannelPool.getConnId(ctx.channel());
        if (StringUtil.isNullOrEmpty(connIdAt)) {
            ctx.channel().close();
            return;
        }
        String[] split = connIdAt.split("@");
        String tuunelConnId = split[0];
        String connId = split[1];
        copyRequestMessage.setConnId(connId);
        int length = buf.readableBytes();
        byte[] data = new byte[length];
        buf.readBytes(data);
        ReferenceCountUtil.release(buf);
        copyRequestMessage.setData(data);
        Channel tunnel = TunnelChannelPool.getChannel(tuunelConnId);
        if (Objects.nonNull(tunnel)) {
            tunnel.writeAndFlush(copyRequestMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("err", cause);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }
}
