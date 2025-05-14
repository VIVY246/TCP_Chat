package com.chattool;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.chattool.util.ChatLogger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class ChatClientHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")); // メッセージ送信時刻を取得
        System.out.println("[" + sendTime + "] " + msg); // メッセージをコンソールに出力

        ChatLogger.log(ctx.name(),"SEND", (String) msg); // ログ出力
        msg = msg + "\n"; // メッセージの末尾に改行を追加
        ctx.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if(cause instanceof IOException){
            System.err.println("リモートホストによって接続が切断されました");
        }

        System.err.println("エラーが発生しました: " + cause.getMessage()); // エラーメッセージを出力
        ctx.close(); // チャネルを閉じる
    }
}