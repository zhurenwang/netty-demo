package com.candy.pool;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.util.internal.StringUtil;

import java.util.Enumeration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelPool {

    private static final ConcurrentHashMap<String, Channel> poolChannel = new ConcurrentHashMap<>();

    public static void setChannel(String connId, Channel channel) {
        poolChannel.put(connId, channel);
    }

    public static Channel getChannel(String connId) {
        return poolChannel.get(connId);
    }


    public static void remove(String connId) {

        if (poolChannel.containsKey(connId)) {
            poolChannel.remove(connId);
        }
    }

    public static void clear() {
        poolChannel.values().forEach(ChannelOutboundInvoker::close);
        poolChannel.clear();
    }
}
