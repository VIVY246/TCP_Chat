package jp.co.vivy.chattool;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import jp.co.vivy.chattool.util.ChatLogger;
import jp.co.vivy.chattool.util.ErrorLogger;

public class ChatClientHandler extends ChannelOutboundHandlerAdapter {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        String sendTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")); // メッセージ送信時刻を取得
        System.out.println("[" + sendTime + "] " + msg); // メッセージをコンソールに出力

        ChatLogger.logChat(ctx.name(), true, (String) msg); // ログ出力
        msg = msg + "\n"; // メッセージの末尾に改行を追加
        ctx.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws IOException {
        ErrorLogger.logError(ctx, cause.getMessage());
        ctx.close(); // チャネルを閉じる
    }
}