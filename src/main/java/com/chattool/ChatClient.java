package com.chattool;

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

    private static final EventLoopGroup group = new NioEventLoopGroup(); // イベントループグループを作成

    // コンストラクタでホスト名とポート番号を設定
    public ChatClient(String host, int port) {
        this.host_ip = host;
        this.port = port;
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
            f.channel().writeAndFlush(message);

            // 接続が閉じられるまで待機
            f.channel().closeFuture().sync();
        } finally {
        }
    }
    public static void shutdown() {
        group.shutdownGracefully(); // イベントループグループをシャットダウン
    }
}