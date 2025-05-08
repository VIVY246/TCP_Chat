package com.chattool.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class DestinationLoader {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, String> load(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("マッピングファイルが見つかりません: " + path);
        }

        try {
            return mapper.readValue(file, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            throw new IOException("マッピングファイルの読み込み中にエラーが発生しました: " + e.getMessage(), e);
        }
    }
}