package com.campus.lostandfound.web;

import java.util.*;

/**
 * Lightweight JSON parser and serializer utilities with zero external dependencies.
 */
public class JsonUtil {

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new LinkedHashMap<>();
        if (json == null || json.trim().isEmpty()) return map;

        String content = json.trim();
        if (content.startsWith("{") && content.endsWith("}")) {
            content = content.substring(1, content.length() - 1).trim();
        }

        boolean inQuote = false;
        boolean inArray = false;
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean parsingKey = true;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '\"' && (i == 0 || content.charAt(i - 1) != '\\')) {
                inQuote = !inQuote;
                continue;
            }

            if (!inQuote) {
                if (c == '[') inArray = true;
                if (c == ']') inArray = false;
                if (c == ':' && parsingKey) {
                    parsingKey = false;
                    continue;
                }
                if (c == ',' && !inArray) {
                    String k = key.toString().trim();
                    String v = value.toString().trim();
                    if (!k.isEmpty()) {
                        map.put(k, unescape(v));
                    }
                    key.setLength(0);
                    value.setLength(0);
                    parsingKey = true;
                    continue;
                }
            }

            if (parsingKey) {
                key.append(c);
            } else {
                value.append(c);
            }
        }

        String k = key.toString().trim();
        String v = value.toString().trim();
        if (!k.isEmpty()) {
            map.put(k, unescape(v));
        }

        return map;
    }

    private static String unescape(String v) {
        if (v == null) return "";
        if (v.startsWith("\"") && v.endsWith("\"") && v.length() >= 2) {
            v = v.substring(1, v.length() - 1);
        }
        return v.replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }
}
