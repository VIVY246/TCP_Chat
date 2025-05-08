package com.chattool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        try {
            String[] parts = msg.split(":", 3); // "from:to:message"形式を分割
            if (parts.length == 3) {
                String from = parts[0];
                String to = parts[1];
                String message = parts[2];
                System.out.printf("[%s → %s] %s%n", from, to, message);
            } else {
                System.err.println("Invalid message format: " + msg);
            }
        } catch (Exception e) {
            System.err.println("Error processing message: " + msg);
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}