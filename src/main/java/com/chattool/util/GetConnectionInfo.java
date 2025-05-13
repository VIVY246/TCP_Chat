package com.chattool.util;

import java.net.InetSocketAddress;

import io.netty.channel.ChannelHandlerContext;

public class GetConnectionInfo {
    public static String getClientIp(ChannelHandlerContext ctx){
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress(); // クライアントのIPアドレスを取得
        return socketAddress.getAddress().getHostAddress(); // クライアントのIPアドレスを文字列形式で取得
    }

    public static String getClientName(String ip){
        String name = IpToNameResolver.getNameByIp(ip); // IPアドレスからクライアント名を取得
        return name != null ? name : "Unknown"; // クライアント名を返す
    }
}
