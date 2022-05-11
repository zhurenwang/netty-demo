package com.candy.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.candy.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {

    private static final int Magic = 1994;
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //魔数
        out.writeInt(1994);
        //版本
        out.writeByte(1);
        // 序列化方式 0: fastJson
        out.writeByte(0);
        //指令类型 0：connect请求  1；copy请求
        out.writeByte(msg.getMessageType());
        //对齐填充
        byte[] bytes = {1,1,1,1,1};
        out.writeBytes(bytes);
        String json = JSON.toJSONString(msg);
        //长度
        out.writeInt(json.getBytes(StandardCharsets.UTF_8).length);
        //对象
        out.writeBytes(json.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        //不认识的协议直接关闭channel
        if (magicNum != Magic) {
            ctx.channel().close();
            return;
        }
        byte version = in.readByte();
        byte serialType = in.readByte();
        byte commandType = in.readByte();
        ByteBuf byteBuf = in.readBytes(5);
        //此处为对齐填充的数据 会返回一个新的byteBuf 由于此byteBuf不会向下传递 故不会被回收 所以需要手动释放
        ReferenceCountUtil.release(byteBuf);
        int length = in.readInt();
        byte[] objectBytes = new byte[length];
        in.readBytes(objectBytes);
        String objectStr = new String(objectBytes, StandardCharsets.UTF_8);
        Message message = JSONObject.parseObject(objectStr, Message.getClass(commandType));
        out.add(message);
        /*log.info("magicNum: {}, version: {}, serialType: {}, commandType: {}, length: {}, message: {}",
                magicNum, version, serialType, commandType, length, JSON.toJSONString(message, true));*/
    }
}
