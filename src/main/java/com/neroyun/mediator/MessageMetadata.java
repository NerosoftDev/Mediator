package com.neroyun.mediator;

import java.util.HashMap;
import java.util.Map;

public class MessageMetadata {
    private final Map<String, Object> metadata = new HashMap<>();

    public Object get(String key) {
        return metadata.get(key);
    }

    public void set(String key, Object value) {
        metadata.put(key, value);
    }
}
