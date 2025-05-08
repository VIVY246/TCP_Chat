package com;

import com.chattool.ChatClient;
import com.chattool.ChatServer;
import com.chattool.util.DestinationLoader;

import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 50000;

        System.out.println("サーバーを起動します...");
        new Thread(() -> {
            try {
                new ChatServer(port).run();
                System.out.println("サーバーが正常に起動しました");
            } catch (InterruptedException e) {
                System.err.println("サーバーの起動中にエラーが発生しました: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
        
        System.out.println("mappings.jsonを読み込みます...");
        String mappingsFile = "JSON/mappings.json";
        Map<String, String> mapping = DestinationLoader.load(mappingsFile);
        System.out.println("mappings.jsonの読み込みが完了しました");
        
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("あなたの名前を入力してください: ");
            String from = scanner.nextLine();
            System.out.println("名前が入力されました: " + from);
        
            while (true) {
                System.out.print("宛先を入力してください: ");
                String to = scanner.nextLine();
                System.out.println("宛先が入力されました: " + to);
        
                System.out.print("メッセージを入力してください: ");
                String msg = scanner.nextLine();
                System.out.println("メッセージが入力されました: " + msg);
        
                String ip = mapping.get(to);
                if (ip == null) {
                    System.out.println("❌ 宛先が見つかりません");
                    continue;
                }
        
                System.out.println("メッセージを送信します...");
                new ChatClient(ip, port).send(from, to, msg);
                System.out.println("メッセージが送信されました");
            }
        }
    }
}