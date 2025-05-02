package com.myproject.cooking1.entities;

import java.util.HashMap;
import java.util.Map;

public class TestContext {
    private static final Map<String, Object> context = new HashMap<>();

    public static void set(String key, Object value) {
        context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key, Class<T> type) {
        Object value = context.get(key);
        if (value == null) {
            throw new IllegalStateException("No value set in TestContext for key: " + key);
        }
        if (!type.isInstance(value)) {
            throw new ClassCastException("Value for key '" + key + "' is not of type " + type.getName());
        }
        return (T) value;
    }

    public static void clear() {
        context.clear();
    }
}
