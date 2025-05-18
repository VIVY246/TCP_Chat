package jp.co.vivy.chattool;


import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.co.vivy.chattool.model.Destination;
import jp.co.vivy.chattool.util.DestinationLoader;
import jp.co.vivy.chattool.util.ErrorLogger;
import jp.co.vivy.chattool.util.ToDestinationByJson;

public class ChatToolManager {
    private static final Map<String, ExecutorService> sendExecutors = new ConcurrentHashMap<>(); // 宛先ごとのExecutorServiceを管理するマップ
    private static final Map<String, ChatClient> chatClients = new ConcurrentHashMap<>(); // 宛先ごとのChatClientを管理するマップ

    public static void main(String[] args) throws Exception {
        String mappingsFile = "JSON/mappings.json"; // マッピングファイルのパス
        Map<String, Destination> mapping = ToDestinationByJson.load(mappingsFile); // 宛先名とDestinationのマッピングをロード

        final int MESSAGE_SENT_AFTER_LATENCY_MS = 200; // メッセージ送信後の遅延時間
        CountDownLatch serverStartedLatch = new CountDownLatch(1); // スレッドの同期を行うためのカウントダウンラッチ

        Map<String, String> reverseMap = DestinationLoader.buildReverseMap(mapping); // 宛先名とIPアドレスの逆マッピングを作成
        DestinationLoader.setReverseMap(reverseMap); // 逆マッピングを設定

        Destination dest = null;
        String to = null;

        String hostName = "O";
        Destination myDest = mapping.get(hostName); // 自分の宛先を取得
        if(myDest == null) {
            System.out.println("自分の宛先が見つかりません");
            System.exit(0);;
        }

        String myIp = myDest.getIpAddress(); // 自分のIPアドレスを取得
        int myPort = myDest.getPort(); // 自分のポート番号を取得
        
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

        serverStartedLatch.await(); // サーバーが起動するまで待機
        
        // ユーザー入力を受け付けるためのScannerを作成
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                Thread.sleep(MESSAGE_SENT_AFTER_LATENCY_MS); // メッセージ送信後の遅延をシミュレート

                if(Objects.isNull(dest)) {
                    // 宛先がnullの場合、宛先名を入力するように促す
                    System.out.println("宛先名を入力してください");
                    to = scanner.nextLine(); // 入力された宛先名を取得
                    dest = mapping.get(to); // 宛先名からDestinationを取得
                    if(dest == null) {
                        System.out.println("宛先名が見つかりません");
                        continue; // 次の入力を待つ
                    }
                } 

                String clientIp = dest.getIpAddress(); // 宛先のIPアドレスを取得
                int clientPort = dest.getPort(); // 宛先のポート番号を取得
                if(Objects.isNull(clientIp) || clientPort <= 0 || clientPort > 65535) {
                    System.out.println("宛先のIPアドレスまたはポート番号が無効です");
                    continue; // 次の入力を待つ
                }

                String msg = null;

                while(true){
                    // メッセージを入力するように促す
                    System.out.println("メッセージを入力してください");
                    msg = scanner.nextLine(); // 入力されたメッセージを取得
                    if(msg.isEmpty()) {
                        System.out.println("メッセージが空です");
                        continue; // 次の入力を待つ
                    }else{
                        break; // メッセージが空でない場合、ループを抜ける
                    }
                }

            boolean isReconnect = false; // 再接続フラグ

            if(msg.equals("exit")) {
                dest = null; // 宛先をnullに設定
                while (true) {
                    System.out.println("再接続しますか？ (yes / no)");
                    msg = scanner.nextLine(); // 再接続の確認メッセージを取得
                    switch (msg) {
                        case "yes":
                            isReconnect = true; // 再接続フラグをtrueに設定                            
                            break;
                        case "no":
                            isReconnect = false; // 再接続フラグをfalseに設定
                            break;
                        default:
                            continue; // 無効な入力の場合、次の入力を待つ
                    }
                    break; // 有効な入力の場合、ループを抜ける
                }
                if(isReconnect) {
                    continue; // 再接続する場合、次の入力を待つ
                } else {
                    break; // 終了する場合、ループを抜ける
                }
            }

            String sendMsg = msg;
            String sendTo = to;
            System.out.println("\nメッセージを送信します…");

            Future<Boolean> future = sendExecutors
                .computeIfAbsent(to, threads -> Executors.newSingleThreadExecutor())
                .submit(() -> {
                    try{
                        ChatClient client = chatClients.computeIfAbsent(sendTo, k -> new ChatClient(clientIp, clientPort));
                        client.send(sendMsg); // メッセージを送信
                        return true; // 送信成功
                    }catch (Exception e) {
                        System.err.println("メッセージの送信中にエラーが発生しました: " + e.getMessage()); // エラーメッセージを出力
                        ErrorLogger.logError(clientIp, e.getMessage()); // エラーをログに記録
                        return false; // 送信失敗
                    }
                });

                // 送信結果を待機
                if(!future.get()) {
                    dest = null; // 宛先をnullに設定
                    continue; // 次の入力を待つ
                }
            }
        }finally{
            // ExecutorServiceをシャットダウン
            for (ExecutorService executor : sendExecutors.values()) {
                executor.shutdown(); // ExecutorServiceをシャットダウン
            }
            // ChatClientをクローズ
            ChatClient.shutdown(); // ChatClientをクローズ
            System.exit(0);
        }
    }
}