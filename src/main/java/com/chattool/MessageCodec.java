package com.chattool;

import com.chattool.model.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.CharsetUtil;
import java.util.List;

public class MessageCodec extends MessageToMessageCodec<ByteBuf, ChatMessage> {
    private final ObjectMapper mapper = new ObjectMapper();

    // デコード: ByteBuf → ChatMessage
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        String json = msg.toString(CharsetUtil.UTF_8).trim();
        if (!json.isEmpty()) {
            out.add(mapper.readValue(json, ChatMessage.class));
        }
    }
    
    // エンコード: ChatMessage → ByteBuf
    @Override
    protected void encode(ChannelHandlerContext ctx, ChatMessage msg, List<Object> out) throws Exception {
        String json = mapper.writeValueAsString(msg) + "\n";
        ByteBuf encoded = ctx.alloc().buffer();
        encoded.writeBytes(json.getBytes(CharsetUtil.UTF_8));
        out.add(encoded);
    }
    
}