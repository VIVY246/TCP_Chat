package com.chattool;

import java.util.concurrent.CountDownLatch;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

// チャットサーバーを起動するクラス
public class ChatServer {
    private final String myIp; // サーバーのホスト名またはIPアドレス
    private final int port; // サーバーが待ち受けるポート番号
    private final CountDownLatch latch; // スレッドの同期を行うためのカウントダウンラッチ

    // コンストラクタでポート番号を設定
    public ChatServer(String myIp, int port, CountDownLatch latch) {
        this.myIp = myIp; // サーバーのホスト名またはIPアドレスを設定
        this.port = port;
        this.latch = latch; // スレッドの同期を行うためのカウントダウンラッチを設定
    }

    // サーバーを起動するメソッド
    public void run() throws InterruptedException {
        // 接続を待ち受けるためのスレッドグループを作成
        EventLoopGroup boss = new NioEventLoopGroup(); // 接続を受け付けるスレッドグループ
        EventLoopGroup worker = new NioEventLoopGroup(); // データ処理を行うスレッドグループ

        try {
            // サーバーの初期化と設定を行う
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, worker) // スレッドグループを設定
             .channel(NioServerSocketChannel.class) // サーバーソケットのチャネルタイプを指定
             .childHandler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 protected void initChannel(SocketChannel ch) {
                     // クライアントからの接続ごとにパイプラインを設定
                     ch.pipeline()
                       .addLast(new LineBasedFrameDecoder(8192)) // 行単位でメッセージを分割
                       .addLast(new StringDecoder(CharsetUtil.UTF_8)) // UTF-8で文字列にデコード
                       .addLast(new ChatServerHandler()); // メッセージ処理を行うハンドラを追加
                 }
             });

            // サーバーを指定したポートでバインドして起動
            ChannelFuture f = b.bind(myIp, port).sync();
            System.out.println("ChatServer started on port " + port); // サーバー起動メッセージを出力

            latch.countDown(); // カウントダウンラッチをデクリメント

            // サーバーソケットが閉じられるまで待機
            f.channel().closeFuture().sync();
        } finally {
            // サーバー停止時にスレッドグループをシャットダウン
            System.out.println("ChatServer shutting down...");
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}