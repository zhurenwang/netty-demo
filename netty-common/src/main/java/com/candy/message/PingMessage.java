package com.candy.message;

public class PingMessage extends Message {
    @Override
    public int getMessageType() {
        return PingMessageType;
    }
}
