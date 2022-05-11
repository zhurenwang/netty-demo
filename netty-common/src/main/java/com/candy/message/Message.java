package com.candy.message;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public abstract class Message implements Serializable {

    private int messageType;
    private static Map<Integer, Class<? extends Message>> map = new HashMap();

    public static final int ConnectRequestMessageType = 0;
    public static final int ConnectResponseMessageType = 1;
    public static final int CopyRequestMessageType = 2;
    public static final int PingMessageType = 3;
    public static final int PongMessageType = 4;
    public static final int CloseChannelRequestMessageType = 5;

    static {
        map.put(ConnectRequestMessageType, ConnectRequestMessage.class);
        map.put(ConnectResponseMessageType, ConnectResponseMessage.class);
        map.put(CopyRequestMessageType, CopyRequestMessage.class);
        map.put(PingMessageType, PingMessage.class);
        map.put(PongMessageType, PongMessage.class);
        map.put(CloseChannelRequestMessageType, CloseChannelRequestMessage.class);
    }
    public abstract int getMessageType();

    public static Class<? extends Message> getClass(int messageType) {
        return map.get(messageType);
    }
}
