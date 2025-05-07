package com;

import com.chattool.ChatClient;
import com.chattool.ChatServer;
import com.chattool.util.DestinationLoader;
import com.chattool.model.ChatMessage;

import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 50000;

        new Thread(() -> {
            try {
                new ChatServer(port).run();
            } catch (InterruptedException e) {
                System.err.println("サーバーの起動中にエラーが発生しました: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("アプリケーションを終了します...");
            // 必要に応じてサーバーの停止処理を追加
        }));

        String mappingsFile = "mappings.json"; // 必要に応じて設定ファイルや引数から取得
        Map<String, String> mapping = DestinationLoader.load(mappingsFile);

        try (Scanner scanner = new Scanner(System.in)) { // try-with-resources を使用
            System.out.print("あなたの名前: ");
            String from = scanner.nextLine();

            while (true) {
                System.out.print("宛先名: ");
                String to = scanner.nextLine();
                System.out.print("メッセージ: ");
                String msg = scanner.nextLine();

                String ip = mapping.get(to);
                if (ip == null) {
                    System.out.println("❌ 宛先が見つかりません");
                    continue;
                }

                ChatMessage message = new ChatMessage(from, to, msg, System.currentTimeMillis());
                new ChatClient(ip, port).send(message);
            }
        }
    }
}