package com.chattool;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatServer {
    private int port;

    public ChatServer(int port) {
        this.port = port;
    }

    public void run() throws Exception{
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannnelOnitializer<SocketChannel>() {
                @Override
                //　説明：チャネルの初期化を行うメソッド
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new ChatServer());
                }
            })
            .option(ChannelOption.SO_BACKLOG, 128) //　接続待ちキューのサイズを設定する
            .childOption(ChannelOption.SO_KEEPALIVE, true); //　接続を維持するオプションを設定する

            ChannelFuture f = b.bind(port).sync(); //　指定したポートでバインドする

            f,channnel().closeFuture().sync(); //　チャネルが閉じるまで待機する
        } finally {
            workerGroup.shutdownGracefully(); //　ワーカースレッドグループをシャットダウンする
            bossGroup.shutdownGracefully(); //　ボススレッドグループをシャットダウンする
        }
    }
    //　説明：メインメソッド
    //　このメソッドは、サーバーを起動するためのエントリーポイントです。
    //　コマンドライン引数からポート番号を取得し、DiscardServerのインスタンスを作成してrunメソッドを呼び出します。
    //　例外が発生した場合は、スタックトレースを出力します。
    public static void main(String[] args) throws Exception {
        int port = 50000; //　ポート番号を指定されなければ50000を使用する
        
        if(args.length > 0){
            port = Integer.parseInt(args[0]); //　コマンドライン引数からポート番号を取得する
        }
        new DiscardServer(port).run(); //　DiscardServerのインスタンスを作成し、runメソッドを呼び出す
    }
}
