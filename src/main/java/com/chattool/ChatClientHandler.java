package com.chattool;

import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandlerContext;

// クライアント側で受信したメッセージを処理するハンドラクラス
public class ChatClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        // サーバーから受信したメッセージをコンソールに出力
        System.out.println("📨 受信メッセージ: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // ハンドラ内で例外が発生した場合の処理
        System.err.println("通信エラー: " + cause.getMessage()); // エラーメッセージを出力
        ctx.close(); // 接続を閉じる
    }
}