package com.candy.message;

public class PongMessage extends Message {
    @Override
    public int getMessageType() {
        return PongMessageType;
    }
}
