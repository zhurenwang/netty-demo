package com.candy.handler;

import com.candy.protocol.CandyLengthFieldBasedFrameDecoder;
import com.candy.protocol.MessageCodec;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import javax.net.ssl.SSLException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CandyChannelInitializer extends ChannelInitializer<NioSocketChannel> {

    private SslContext sslContext;

    public CandyChannelInitializer() {
        try {
            InputStream keyFile = this.getClass().getClassLoader().getResourceAsStream("privkey.pem");
            InputStream keyCertChainFile = this.getClass().getClassLoader().getResourceAsStream("cacert.pem");
            //引入SSL安全验证
            SslContext sslCtx = SslContextBuilder.forServer(keyCertChainFile, keyFile).clientAuth(ClientAuth.NONE).build();
            this.sslContext = sslCtx;
        } catch (SSLException e) {
            e.printStackTrace();
            log.error("e", e);
        }
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addFirst(sslContext.newHandler(ch.alloc()));
        ch.pipeline().addLast(new CandyLengthFieldBasedFrameDecoder());
        //ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        ch.pipeline().addLast(new MessageCodec());
        ch.pipeline().addLast(new IdleStateHandler(60*30, 0, 0, TimeUnit.SECONDS));
        ch.pipeline().addLast(new CandyIdleStateHandler());
        ch.pipeline().addLast(new PingChannelHandler());
        ch.pipeline().addLast(new ConnectChannelHandler());
        ch.pipeline().addLast(new CopyChannelHandler());
        ch.pipeline().addLast(new CloseChannelHandler());
    }
}
