package com.candy.message;

import lombok.Data;

@Data
public class ConnectRequestMessage extends Message {

    private String host;
    private int port;
    private String connId;
    @Override
    public int getMessageType() {
        return ConnectRequestMessageType;
    }
}
