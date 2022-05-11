package com.candy.pool;

import io.netty.channel.Channel;
import io.netty.util.internal.StringUtil;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelPool {

    private static final ConcurrentHashMap<String, Channel> poolChannel = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Channel, String> poolConnId = new ConcurrentHashMap<>();

    public static void setChannel(String connId, Channel channel) {
        poolChannel.put(connId, channel);
        poolConnId.put(channel, connId);
    }

    public static Channel getChannel(String connId) {
        return poolChannel.get(connId);
    }

    public static String getConnId(Channel channel) {
        return poolConnId.get(channel);
    }

    public static void remove(String connId) {
        Channel channel = poolChannel.remove(connId);
        poolConnId.remove(channel);
    }

    public static void remove(Channel channel) {
        if (poolConnId.containsKey(channel)) {
            String key = poolConnId.remove(channel);
            if (StringUtil.isNullOrEmpty(key)) {
                poolChannel.remove(key);
            }
        }
    }
}
