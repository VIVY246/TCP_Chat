package com.chattool;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

// サーバー側で受信したメッセージを処理するハンドラクラス
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // クライアントから受信したメッセージを処理
        try {
            // メッセージを "from:to:message" 形式で分割
            String[] parts = msg.split(":", 3);
            if (parts.length == 3) {
                // メッセージの各部分を取得
                String from = parts[0]; // 送信者
                String to = parts[1];   // 宛先
                String message = parts[2]; // メッセージ内容

                // メッセージをコンソールに出力
                System.out.printf("[%s → %s] %s%n", from, to, message);
            } else {
                // メッセージ形式が不正な場合のエラーメッセージ
                System.err.println("Invalid message format: " + msg);
            }
        } catch (Exception e) {
            // メッセージ処理中にエラーが発生した場合の処理
            System.err.println("Error processing message: " + msg);
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // ハンドラ内で例外が発生した場合の処理
        cause.printStackTrace(); // 例外のスタックトレースを出力
        ctx.close(); // 接続を閉じる
    }
}