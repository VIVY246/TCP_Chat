package com.chattool;

import com.chattool.model.ChatMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

public class ChatClient {
    private final String host;
    private final int port;

    public ChatClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    public void send(ChatMessage msg) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
    
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
             .channel(NioSocketChannel.class)
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) throws Exception {
                     ch.pipeline()
                       .addLast(new LineBasedFrameDecoder(8192)) // フレーム分割
                       .addLast(new MessageCodec())              // エンコード/デコード
                       .addLast(new ChatClientHandler());         // クライアントハンドラ
                 }
             });
    
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().writeAndFlush(msg); // ChatMessage をそのまま送信
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}