package com.chattool.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

public class ChatLogger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String LOG_DIR = "logs/"; // ログメッセージのフォーマット

    static {
        // ログディレクトリの作成
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs(); // ディレクトリが存在しない場合は作成
        }
    }

    public static void log(String identifier, String direction, String message) {
        // ログメッセージをファイルに書き込む処理
        String timestamp = java.time.LocalDateTime.now().format(formatter); // 現在の時刻を取得
        String safeIdentifier = identifier.replaceAll("[^a-zA-Z0-9]", "_"); // 特殊文字をアンダースコアに置換
        String fileName = LOG_DIR + safeIdentifier + ".log"; // ログファイル名を生成
        File logFile = new File(fileName); // ログファイルオブジェクトを作成

        try {
            if (!logFile.exists()) {
                logFile.createNewFile(); // ログファイルが存在しない場合は新規作成
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                writer.printf("[%s] %s%n", timestamp, direction, message); // ログメッセージをファイルに書き込む
            }
        } catch (IOException e) {
            // エラーハンドリング
            System.err.println("ログファイルの書き込み中にエラーが発生しました: " + e.getMessage());
        }
    }
}
