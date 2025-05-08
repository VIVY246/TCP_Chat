package com;

import com.chattool.ChatClient;
import com.chattool.ChatServer;
import com.chattool.util.DestinationLoader;

import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        int port = 50000; // サーバーとクライアントが通信するためのポート番号

        // サーバーを別スレッドで起動
        System.out.println("サーバーを起動します...");
        new Thread(() -> {
            try {
                // ChatServerを起動し、指定したポートで接続を待ち受ける
                new ChatServer(port).run();
                System.out.println("サーバーが正常に起動しました");
            } catch (InterruptedException e) {
                // サーバー起動中にエラーが発生した場合の処理
                System.err.println("サーバーの起動中にエラーが発生しました: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();

        // 宛先マッピングファイル（mappings.json）を読み込む
        System.out.println("mappings.jsonを読み込みます...");
        String mappingsFile = "JSON/mappings.json"; // マッピングファイルのパス
        Map<String, String> mapping = DestinationLoader.load(mappingsFile); // 宛先名とIPアドレスのマッピングをロード
        System.out.println("mappings.jsonの読み込みが完了しました");

        // ユーザー入力を受け付けるためのScannerを作成
        try (Scanner scanner = new Scanner(System.in)) {
            // ユーザーの名前を入力
            System.out.print("あなたの名前を入力してください: ");
            String from = scanner.nextLine(); // 入力された名前を取得
            System.out.println("名前が入力されました: " + from);

            // 無限ループでメッセージ送信処理を繰り返す
            while (true) {
                // 宛先名を入力
                System.out.print("宛先を入力してください: ");
                String to = scanner.nextLine(); // 入力された宛先名を取得
                System.out.println("宛先が入力されました: " + to);

                // メッセージ内容を入力
                System.out.print("メッセージを入力してください: ");
                String msg = scanner.nextLine(); // 入力されたメッセージを取得
                System.out.println("メッセージが入力されました: " + msg);

                // 宛先名に対応するIPアドレスをマッピングから取得
                String ip = mapping.get(to);
                if (ip == null) {
                    // 宛先が見つからない場合のエラーメッセージ
                    System.out.println("❌ 宛先が見つかりません");
                    continue; // 次の入力を待つ
                }

                // メッセージを送信
                System.out.println("メッセージを送信します...");
                new ChatClient(ip, port).send(from, to, msg); // ChatClientを使用してメッセージを送信
                System.out.println("メッセージが送信されました");
            }
        }
    }
}