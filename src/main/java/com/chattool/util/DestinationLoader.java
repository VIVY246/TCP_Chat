package com.chattool.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

// 宛先マッピングファイルを読み込むユーティリティクラス
public class DestinationLoader {
    // JSONデータを処理するためのObjectMapperインスタンス
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * 指定されたパスのJSONファイルを読み込み、宛先名とIPアドレスのマッピングを返す
     *
     * @param path マッピングファイルのパス
     * @return 宛先名とIPアドレスのマッピングを格納したMap
     * @throws Exception ファイルが存在しない場合や読み込みエラーが発生した場合にスローされる
     */
    public static Map<String, String> load(String path) throws Exception {
        // 指定されたパスのファイルオブジェクトを作成
        File file = new File(path);

        // ファイルが存在しない場合は例外をスロー
        if (!file.exists()) {
            throw new IOException("マッピングファイルが見つかりません: " + path);
        }

        try {
            // JSONファイルを読み込み、Map<String, String>形式に変換して返す
            return mapper.readValue(file, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            // JSONの読み込み中にエラーが発生した場合は例外をスロー
            throw new IOException("マッピングファイルの読み込み中にエラーが発生しました: " + e.getMessage(), e);
        }
    }
}