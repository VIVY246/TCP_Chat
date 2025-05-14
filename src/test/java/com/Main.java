package com;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import com.chattool.ChatClient;
import com.chattool.ChatServer;
import com.chattool.model.Destination;
import com.chattool.util.DestinationLoader;
import com.chattool.util.IpToNameResolver;

public class Main {
    public static void main(String[] args) throws Exception {
        String mappingsFile = "JSON/mappings.json"; // マッピングファイルのパス
        Map<String, Destination> mapping = DestinationLoader.load(mappingsFile); // 宛先名とDestinationのマッピングをロード

        String hostName = "O";
        Destination myDest = mapping.get(hostName); // 自分の宛先を取得
        if(myDest == null) {
            System.out.println("自分の宛先が見つかりません");
            System.exit(0);;
        }

        String myIp = myDest.getIpAddress(); // 自分のIPアドレスを取得
        int myPort = myDest.getPort(); // 自分のポート番号を取得
        final int MESSAGE_SENT_AFTER_LATENCY_MS = 200;
        CountDownLatch serverStartedLatch = new CountDownLatch(1); // スレッドの同期を行うためのカウントダウンラッチ

        // サーバーを別スレッドで起動
        System.out.println("サーバーを起動します...");
        new Thread(() -> {
            try {
                // ChatServerを起動し、指定したポートで接続を待ち受ける
                new ChatServer(myIp, myPort, serverStartedLatch).run();
                System.out.println("サーバーが正常に起動しました");
            } catch (InterruptedException e) {
                // サーバー起動中にエラーが発生した場合の処理
                System.err.println("サーバーの起動中にエラーが発生しました: " + e.getMessage());
                //e.printStackTrace();
            }
        }).start();

        Map<String, String> reverseMap = IpToNameResolver.buildReverseMap(mapping); // 宛先名とIPアドレスの逆マッピングを作成
        IpToNameResolver.setReverseMap(reverseMap); // 逆マッピングを設定

        serverStartedLatch.await(); // サーバーが起動するまで待機
        
        // ユーザー入力を受け付けるためのScannerを作成
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                Thread.sleep(MESSAGE_SENT_AFTER_LATENCY_MS); // メッセージ送信後の遅延をシミュレート

                System.out.print("宛先を入力してください: ");
                String to = scanner.nextLine(); // 入力された宛先名を取得
                Destination dest = mapping.get(to); // 宛先名からDestinationを取得
                if (dest == null) {
                    // 宛先が見つからない場合のエラーメッセージ
                    System.out.println("宛先が見つかりません");
                    continue; // 次の入力を待つ
                }

                String ip = dest.getIpAddress(); // 宛先のIPアドレスを取得
                int port = dest.getPort(); // 宛先のポート番号を取得
                if(ip == null || port <= 0 || port > 65535) {
                    System.out.println("宛先のIPアドレスまたはポート番号が無効です");
                    continue; // 次の入力を待つ
                }
                
                System.out.print("メッセージを入力してください: ");
                String msg = scanner.nextLine(); // 入力されたメッセージを取得
                while(msg.trim().isEmpty()) {
                    System.out.print("\nメッセージを入力してください: ");
                    msg = scanner.nextLine().trim(); // 空でないメッセージを取得
                }
                String sendMsg = msg;

                System.out.println("メッセージを送信します...");

                new Thread(() -> {
                    try{
                        new ChatClient(myIp, myPort).send(sendMsg);
                    } catch (InterruptedException e) {
                        // メッセージ送信中にエラーが発生した場合の処理
                        System.err.println("メッセージの送信中にエラーが発生しました: " + e.getMessage());
                        //e.printStackTrace();
                    }
                }).start();
            }
        }finally {
            ChatClient.shutdown(); // イベントループグループをシャットダウン
        }
    }
}