package com.candy.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class CandyLengthFieldBasedFrameDecoder extends LengthFieldBasedFrameDecoder {

    public CandyLengthFieldBasedFrameDecoder() {
        super(Integer.MAX_VALUE, 12, 4, 0, 0);
    }
}
