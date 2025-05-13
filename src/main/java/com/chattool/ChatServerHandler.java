package com.chattool;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.chattool.util.ChatLogger;
import com.chattool.util.ConnectionLogger;
import com.chattool.util.GetConnectionInfo;
import com.chattool.util.IpToNameResolver;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

// サーバー側で受信したメッセージを処理するハンドラクラス
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress(); // クライアントのIPアドレスを取得
            String clientIp = remoteAddress.getAddress().getHostAddress(); // クライアントのIPアドレスを文字列形式で取得

            String clientName =IpToNameResolver.getNameByIp(clientIp); // IPアドレスからクライアント名を取得
            if (clientName == null) {
                clientName = "Unknown"; // クライアント名が取得できない場合は"Unknown"を設定
            }

            String receiveTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // メッセージ受信時刻を取得
            System.out.println("📩 [" + receiveTime + "] メッセージを受信しました"); // 受信したメッセージをコンソールに出力

            ChatLogger.log(clientName, "RECEIVE", msg);

            System.out.println("from: " + clientName + " (" + clientIp + ")"); // 送信元のクライアント名とIPアドレスを出力
            System.out.println("message: " + msg); // 受信したメッセージを出力
        } catch (Exception e) {
            e.printStackTrace(); // エラーメッセージを出力
            ctx.close(); // 接続を閉じる
        }
    }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // クライアントが接続したときの処理
            String clientIp = GetConnectionInfo.getClientIp(ctx); // クライアントのIPアドレスを取得
            String clientName = GetConnectionInfo.getClientName(clientIp); // クライアント名を取得
            
            System.out.println("クライアント接続: " + clientName + " (" + clientIp + ")"); // クライアントのIPアドレスを出力
            ConnectionLogger.logConnection(clientName, clientIp); // 接続ログを記録

            super.channelActive(ctx); // 親クラスのメソッドを呼び出す
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            // クライアントが切断したときの処理
            String clientIp = GetConnectionInfo.getClientIp(ctx); // クライアントのIPアドレスを取得
            String clientName = GetConnectionInfo.getClientName(clientIp); // クライアント名を取得
            
            System.out.println("クライアント切断: " + clientName + " (" + clientIp + ")"); // クライアントのIPアドレスを出力
            ConnectionLogger.logDisconnection(clientName, clientIp); // 切断ログを記録

            super.channelInactive(ctx); // 親クラスのメソッドを呼び出す
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace(); // エラーメッセージを出力
            ctx.close(); // 接続を閉じる
        }
}