package com.candy.message;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
public class CloseChannelRequestMessage extends Message {

    private String connId;

    @Override
    public int getMessageType() {
        return CloseChannelRequestMessageType;
    }
}
