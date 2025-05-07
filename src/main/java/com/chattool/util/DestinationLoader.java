package com.chattool.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;

public class DestinationLoader {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, String> load(String path) throws Exception {
        return mapper.readValue(new File(path), new TypeReference<Map<String, String>>() {});
    }
}