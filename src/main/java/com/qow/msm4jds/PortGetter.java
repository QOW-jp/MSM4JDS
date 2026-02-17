package com.qow.msms4j;

import com.qow.util.Property;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PortGetter {
    public static int get(Property property) {
        return get("", property);
    }

    public static int get(String pre, Property property) {
        boolean autoPorting = Boolean.parseBoolean(property.get(pre + "auto-porting"));
        if (autoPorting) {
            Path portTempPath = Paths.get(property.get(pre + "port-temp"));
            try {
                List<String> lines = Files.readAllLines(portTempPath, StandardCharsets.UTF_8);
                return Integer.parseInt(lines.get(0));
            } catch (IOException e) {
                return 0;
            }
        } else {
            return Integer.parseInt(property.get(pre + "port"));
        }
    }

}
