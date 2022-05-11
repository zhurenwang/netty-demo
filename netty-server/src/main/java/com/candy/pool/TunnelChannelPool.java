package com.candy.pool;

import io.netty.channel.Channel;
import io.netty.util.internal.StringUtil;

import java.util.concurrent.ConcurrentHashMap;

public class TunnelChannelPool {

    private static final ConcurrentHashMap<String, Channel> poolChannel = new ConcurrentHashMap<>();

    public static void setChannel(String connId, Channel channel) {
        poolChannel.put(connId, channel);
    }

    public static Channel getChannel(String connId) {
        return poolChannel.get(connId);
    }

    public static void remove(String connId) {
        poolChannel.remove(connId);
    }

}
