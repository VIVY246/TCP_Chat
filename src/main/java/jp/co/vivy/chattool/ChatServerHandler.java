package jp.co.vivy.chattool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jp.co.vivy.chattool.util.ChatLogger;
import jp.co.vivy.chattool.util.ConnectionLogger;
import jp.co.vivy.chattool.util.ErrorLogger;
import jp.co.vivy.chattool.util.GetConnectionInfo;

// サーバー側で受信したメッセージを処理するハンドラクラス
public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        try {
            String clientIp = GetConnectionInfo.getClientIp(ctx); // ユーティリティクラスでIPアドレスを取得
            String clientName = GetConnectionInfo.getClientName(clientIp); // IPアドレスからクライアント名を取得

            String receiveTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")); // メッセージ受信時刻を取得
            System.out.println("[" + receiveTime + "] メッセージを受信しました"); // 受信したメッセージをコンソールに出力

            ChatLogger.logChat(clientName, false, msg);

            System.out.println("from: " + clientName + " (" + clientIp + ")"); // 送信元のクライアント名とIPアドレスを出力
            System.out.println("message: " + msg); // 受信したメッセージを出力
        } catch (Exception e) {
            System.err.println("メッセージの処理中にエラーが発生しました: " + e.getMessage()); // エラーメッセージを出力
            e.printStackTrace(); // 接続を閉じる
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String clientIp = GetConnectionInfo.getClientIp(ctx); // クライアントのIPアドレスを取得
        String clientName = GetConnectionInfo.getClientName(clientIp); // クライアント名を取得
        
        System.out.println("クライアント接続: " + clientName + " (" + clientIp + ")"); // クライアントのIPアドレスを出力
        ConnectionLogger.logConnection(clientName, clientIp); // 接続ログを記録

        super.channelActive(ctx); // 親クラスのメソッドを呼び出す
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String clientIp = GetConnectionInfo.getClientIp(ctx); // クライアントのIPアドレスを取得
        String clientName = GetConnectionInfo.getClientName(clientIp); // クライアント名を取得
        
        System.out.println("クライアント切断: " + clientName + " (" + clientIp + ")"); // クライアントのIPアドレスを出力
        ConnectionLogger.logDisconnection(clientName, clientIp); // 切断ログを記録

        super.channelInactive(ctx); // 親クラスのメソッドを呼び出す
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace(); // エラーメッセージを出力
        ErrorLogger.logError(ctx, cause.getMessage());
        ctx.close(); // 接続を閉じる
    }
}