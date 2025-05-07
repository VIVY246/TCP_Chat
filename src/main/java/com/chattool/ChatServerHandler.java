package com.chattool;
import com.chattool.model.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        try {
            ChatMessage chatMessage = mapper.readValue(msg.trim(), ChatMessage.class);
            System.out.printf("[%s → %s] %s%n", chatMessage.from, chatMessage.to, chatMessage.message);
        } catch (Exception e) {
            System.err.println("Invalid JSON received: " + msg);
            e.printStackTrace();
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}