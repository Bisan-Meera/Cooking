package com.myproject.cooking1.entities;

import java.util.HashMap;
import java.util.Map;

public class TestContext {
    private static final Map<String, Object> context = new HashMap<>();

    public static void set(String key, Object value) {
        context.put(key, value);
    }

    public static <T> T get(String key, @org.jetbrains.annotations.NotNull Class<T> type) {
        return type.cast(context.get(key));
    }

    public static void clear() {
        context.clear();
    }
}
