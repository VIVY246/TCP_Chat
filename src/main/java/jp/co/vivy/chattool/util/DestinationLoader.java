package jp.co.vivy.chattool.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jp.co.vivy.chattool.model.Destination;

public class DestinationLoader {
    
    private static final Map<String, String> toNameByIpMap = new ConcurrentHashMap<>();

    /**
     * 宛先名とIPアドレスの逆マッピングを設定
     * @param map
     */
    public static void setReverseMap(Map<String, String> map) {
        toNameByIpMap.clear();  // 既存のマップをクリア
        toNameByIpMap.putAll(map);  // 新しいマップを追加
    }

    /**
     * IPアドレスから名前を取得
     * @param ipAddress 
     * @return 名前(String)
     */
    public static String getNameByIp(String ipAddress) {
        return toNameByIpMap.get(ipAddress);  // IPアドレスから名前を取得
    }

    /**
     * 宛先名とDestinationのマッピングを逆にして、IPアドレスをキーにしたマップを作成
     * @param nameToDest 宛先名とDestinationのマッピング
     * @return IPアドレスをキーにしたマップ
     */
    public static Map<String, String> buildReverseMap(Map<String, Destination> nameToDest) {
        Map<String, String> reverseMap = new ConcurrentHashMap<>();
        for (Map.Entry<String, Destination> entry : nameToDest.entrySet()) {
            reverseMap.put(entry.getValue().getIpAddress(), entry.getKey());  // 名前とIPアドレスの逆マップを作成
        }
        return reverseMap;  // 逆マップを返す
    }
}