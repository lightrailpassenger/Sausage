package io.github.lightrailpassenger.sausage.utils;

import java.util.Map;
import java.util.HashMap;

public class SettingUtil {
    public static Map<String, String[]> parseValueAsMap(String str) {
        Map<String, String[]> map = new HashMap<>();

        for (String items: str.split(";\\s?")) {
            String[] entry = items.split(":", 2);

            map.put(entry[0], entry[1].split(",\\s?"));
        }

        return map;
    }

    public static String serializeMap(Map<String, String[]> map) {
        StringBuilder builder = new StringBuilder();

        for (Map.Entry<String, String[]> entry: map.entrySet()) {
            builder.append(entry.getKey());
            builder.append(":");

            String[] values = entry.getValue();

            for (String value: values) {
                builder.append(value);
                builder.append(",");
            }

            builder.setCharAt(builder.length() - 1, ';');
        }

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }
}
