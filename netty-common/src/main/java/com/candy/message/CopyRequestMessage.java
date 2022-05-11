package com.candy.message;

import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public class CopyRequestMessage extends Message {

    private String connId;
    private byte[] data;

    @Override
    public int getMessageType() {
        return CopyRequestMessageType;
    }
}
