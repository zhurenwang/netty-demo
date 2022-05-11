package com.candy.message;

import lombok.Data;

@Data
public class ConnectResponseMessage extends Message {

    private String host;
    private int port;
    private boolean isSuccess;
    private String connId;
    @Override
    public int getMessageType() {
        return ConnectResponseMessageType;
    }
}
