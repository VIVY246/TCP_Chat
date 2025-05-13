package com.chattool;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.chattool.util.ChatLogger;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

// チャットクライアントを実装するクラス
public class ChatClient {
    private final String host_ip; // サーバーのホスト名またはIPアドレス
    private final int port;    // サーバーのポート番号
    private final String logKey; // ログ出力用のキー

    private static final EventLoopGroup group = new NioEventLoopGroup(); // イベントループグループを作成

    // コンストラクタでホスト名とポート番号を設定
    public ChatClient(String host, int port, String logKey) {
        this.host_ip = host;
        this.port = port;
        this.logKey = logKey; // ログ出力用のキーを設定
    }

    // サーバーに接続してメッセージを送信するメソッド
    public void send(String message) throws InterruptedException {
        try {
            // クライアントの初期化と設定を行う
            Bootstrap b = new Bootstrap();
            b.group(group) // イベントループグループを設定
             .channel(NioSocketChannel.class) // クライアントソケットのチャネルタイプを指定
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) throws Exception {
                     // クライアントのパイプラインを設定
                     ch.pipeline()
                       .addLast(new LineBasedFrameDecoder(8192)) // 行単位でメッセージを分割
                       .addLast(new StringEncoder(CharsetUtil.UTF_8)) // UTF-8で文字列をエンコード
                       .addLast(new ChatClientHandler()); // メッセージ処理を行うハンドラを追加
                 }
             });

            // サーバーに接続
            ChannelFuture f = b.connect(host_ip, port).sync();

            // サーバーにメッセージを送信
            f.channel().writeAndFlush(message + "\n").addListener(writeFuture -> {
                if (writeFuture.isSuccess()) {
                    String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")); // 現在の日時を取得
                    System.out.println("[ " + sendTime + "] メッセージが送信されました"); // 送信したメッセージをコンソールに出力
                    ChatLogger.log(logKey, "SEND   ", message); // ログ出力
                } else {
                    System.err.println("メッセージの送信に失敗しました: " + writeFuture.cause().getMessage()); // メッセージ送信失敗メッセージを出力
                }
            });

            // 接続が閉じられるまで待機
            f.channel().closeFuture().sync();
        } finally {
        }
    }
    public static void shutdown() {
        group.shutdownGracefully(); // イベントループグループをシャットダウン
    }
}