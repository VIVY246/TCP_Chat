package com.chattool;

import com.chattool.model.ChatMessage;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandlerContext;

public class ChatClientHandler extends SimpleChannelInboundHandler<ChatMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatMessage msg) {
        System.out.println("📨 " + msg);
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("通信エラー: " + cause.getMessage());
        ctx.close();
    }
}