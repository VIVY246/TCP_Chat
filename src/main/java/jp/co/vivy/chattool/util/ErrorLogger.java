package jp.co.vivy.chattool.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import io.netty.channel.ChannelHandlerContext;

public class ErrorLogger {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String LOG_DIR = "logs/";
    private static final String LOG_FILE = LOG_DIR + "error.log";
    private static final String MAPPING_JSONFILE_PATH = "JSON/mappings.json";

    static {
        createLogDirectory();
        createLogFile();
    }

    private static void createLogDirectory() {
        File dir = new File(LOG_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            System.err.println("ログディレクトリの作成に失敗しました: " + LOG_DIR);
        }
    }

    private static void createLogFile() {
        File logFile = new File(LOG_FILE);
        if (!logFile.exists()) {
            try {
                if (!logFile.createNewFile()) {
                    System.err.println("ログファイルの作成に失敗しました: " + LOG_FILE);
                }
            } catch (IOException e) {
                System.err.println("ログファイルの作成中にエラーが発生しました: " + e.getMessage());
            }
        }
    }

    // 1. ChannelHandlerContextから呼び出す場合
    public static void logError(ChannelHandlerContext ctx, String errorMessage) {
        String clientIp = ctx != null ? GetConnectionInfo.getClientIp(ctx) : "Unknown";
        logError(clientIp, errorMessage);
    }

    // 2. IPアドレスから直接呼び出す場合
    public static void logError(String clientIp, String errorMessage) {
        String safeIp = Objects.requireNonNullElse(clientIp, "Unknown");
        String clientName = GetConnectionInfo.getClientName(safeIp);
        logErrorInternal(clientName, safeIp, errorMessage);
    }

    // 3. 共通のログ出力処理
    private static void logErrorInternal(String clientName, String clientIp, String errorMessage) {
        String timeStamp = LocalDateTime.now().format(DATE_FORMATTER);

        if (!isMappingFileExists()) {
            System.err.println("マッピングファイルが存在しません: " + MAPPING_JSONFILE_PATH);
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            writer.printf("[%s] ERROR : %s (%s) - %s%n", timeStamp, clientName, clientIp, errorMessage);
        } catch (IOException e) {
            System.err.println("ログファイルの書き込み中にエラーが発生しました: " + e.getMessage());
        }
    }

    private static boolean isMappingFileExists() {
        File file = new File(MAPPING_JSONFILE_PATH);
        return file.exists();
    }
}
