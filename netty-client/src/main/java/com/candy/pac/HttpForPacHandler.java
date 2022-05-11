package com.candy.pac;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.socksx.SocksPortUnificationServerHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.Objects;

@Slf4j
public class HttpForPacHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(httpRequest.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "application/x-ns-proxy-autoconfig");
            response.headers().set("Content-Disposition", "attachment;filename=proxy.pac");
            InputStream in = this.getClass().getClassLoader().getResourceAsStream("proxy.pac");
            if (Objects.isNull(in)) {
                ctx.channel().close();
                return;
            }
            ByteBuf buffer = ctx.alloc().buffer();
            while (true) {
                byte[] bytes = new byte[1024*10];
                int i = in.read(bytes);
                if (i == -1) {
                    break;
                }
                buffer.writeBytes(bytes, 0, i);
            }
            response.content().writeBytes(buffer);
            log.info("返回pac文件成功");
            ctx.writeAndFlush(response);
            ctx.channel().close();
        }
        if (msg instanceof HttpContent) {

        }
    }
}
