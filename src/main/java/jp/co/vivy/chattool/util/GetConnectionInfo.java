package jp.co.vivy.chattool.util;

import java.net.InetSocketAddress;
import java.util.Objects;

import io.netty.channel.ChannelHandlerContext;

public class GetConnectionInfo {
    /**
     * ChannelHandlerContextからクライアントのIPアドレスを取得するメソッド
     * @param ctx : チャネルハンドラコンテキスト
     * @return  : クライアントのIPアドレス
     */
    public static String getClientIp(ChannelHandlerContext ctx){
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress(); // クライアントのIPアドレスを取得
        return socketAddress.getAddress().getHostAddress(); // クライアントのIPアドレスを文字列形式で取得
    }

    /**
     * ipからクライアント名を取得するメソッド
     * アドレス帳に登録されていない場合は"Unknown"を返す
     * @param ip : クライアントのIPアドレス
     * @return : クライアント名
     */
    public static String getClientName(String ip){
        String name = DestinationLoader.getNameByIp(ip); // IPアドレスからクライアント名を取得
        return Objects.nonNull(name) ? name : "Unknown"; // クライアント名を返す
    }
}
