package jp.co.vivy.chattool.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.co.vivy.chattool.model.Destination;


public class ToDestinationByJson {
    private static final ObjectMapper mapper = new ObjectMapper(); // JSON処理のためのObjectMapper

    public static Map<String, Destination> load(String filePath) throws Exception {
        File file = new File(filePath); // JSONファイルを指定
        if (!file.exists()) { // ファイルが存在しない場合
            throw new IOException("File not found: " + filePath); // エラーメッセージを表示
        }

        try {
            Map<String, Destination> nameToDest = 
                mapper.readValue(file, new TypeReference<Map<String, Destination>>() {}); // JSONファイルを読み込む

            return nameToDest; // 読み込んだマップを返す
        }catch (IOException e) { // JSONの読み込み中にエラーが発生した場合
            throw new IOException("Error reading JSON file: " + e.getMessage(), e); // エラーメッセージを表示
        }
    }
}
