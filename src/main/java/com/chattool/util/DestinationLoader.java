package com.chattool.util;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.chattool.model.Destination;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DestinationLoader {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Destination> load(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException("Destination file not found: " + path);
        }

        try {
            Map<String, Destination> nameToDest = 
                mapper.readValue(file, new TypeReference<Map<String, Destination>>() {});
            return nameToDest;
        } catch (Exception e) {
            throw new IOException("Failed to load destination file: " + e.getMessage(), e);
        }
    }
}