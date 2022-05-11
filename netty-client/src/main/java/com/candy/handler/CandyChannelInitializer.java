package com.candy.handler;

import com.candy.protocol.CandyLengthFieldBasedFrameDecoder;
import com.candy.protocol.MessageCodec;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.io.File;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CandyChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    private SslContext sslContext;

    public CandyChannelInitializer() {
        try {
            SslContext sslCtx = null;
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
            this.sslContext = sslCtx;
        } catch (SSLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addFirst(sslContext.newHandler(ch.alloc()));
        ch.pipeline().addLast(new CandyLengthFieldBasedFrameDecoder());
        ch.pipeline().addLast(new MessageCodec());
        ch.pipeline().addLast(new IdleStateHandler(30, 5, 0, TimeUnit.SECONDS));
        ch.pipeline().addLast(new CandyIdleStateHandler());
        ch.pipeline().addLast(new PongChannelHandler());
        ch.pipeline().addLast(new ConnectorResponseChannelHandler());
        ch.pipeline().addLast(new CopyChannelHandler());
        ch.pipeline().addLast(new CloseChannelHandler());
    }
}
