package com.chattool.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConnectionLogger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String LOG_DIR = "logs/"; // ログメッセージのフォーマット
    private static final String LOG_FILE = LOG_DIR + "connection.log"; // ログファイル名

    static {
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs(); // ディレクトリが存在しない場合は作成
        }
    }

    // 接続されたときのログ記録
    public static void logConnection(String clientName, String ipAddress) {
        String timeStamp = LocalDateTime.now().format(formatter); // 現在の時刻を取得
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.printf("[%s] 接続: %s (%s)%n", timeStamp, clientName, ipAddress); // ログメッセージをファイルに書き込む
        } catch (IOException e) {
            // エラーハンドリング
            System.err.println("ログファイルの書き込み中にエラーが発生しました: " + e.getMessage());
        }
    }

    // 切断されたときのログ記録
    public static void logDisconnection(String clientName, String ipAddress) {
        String timeStamp = LocalDateTime.now().format(formatter); // 現在の時刻を取得
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.printf("[%s] 切断: %s (%s)%n", timeStamp, clientName, ipAddress); // ログメッセージをファイルに書き込む
        } catch (IOException e) {
            // エラーハンドリング
            System.err.println("ログファイルの書き込み中にエラーが発生しました: " + e.getMessage());
        }
    }
}
