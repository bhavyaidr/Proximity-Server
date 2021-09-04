package com.proximity.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class JsonUtil {

    public JsonUtil(){

    }
    public static String getStringFromFile(final String fileName) throws IOException {
        final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        final InputStream in = classLoader.getResourceAsStream(fileName);
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result.toString(StandardCharsets.UTF_8.name());
    }
}
