package com.chattool.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.chattool.model.Destination;


public class IpToNameResolver {
    private static final Map<String, String> ipToNameMap = new ConcurrentHashMap<>();

    public static void setReverseMap(Map<String, String> map) {
        ipToNameMap.clear();
        ipToNameMap.putAll(map);
    }

    public static String getNameByIp(String ipAddress) {
        return ipToNameMap.get(ipAddress);
    }

    public static Map<String, String> buildReverseMap(Map<String, Destination> nameToDest) {
        Map<String, String> reverseMap = new HashMap<>();
        for (Map.Entry<String, Destination> entry : nameToDest.entrySet()) {
            reverseMap.put(entry.getValue().getIpAddress(), entry.getKey());
        }
        return reverseMap;
    }
}
