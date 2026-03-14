package dev.lrxh.neptune.providers.database.impl;

import com.google.gson.JsonParseException;
import dev.lrxh.neptune.utils.ServerUtils;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataDocument {
    public final Map<String, Object> data;

    public DataDocument() {
        this.data = new HashMap<>();
    }

    public DataDocument(Document document) {
        this.data = new HashMap<>();
        for (String key : document.keySet()) {
            Object value = document.get(key);
            if (value instanceof Document) {
                this.data.put(key, new DataDocument((Document) value));
            } else {
                this.data.put(key, value);
            }
        }
    }

    public DataDocument(String jsonString) {
        this.data = new HashMap<>();
        try {
            Document document = Document.parse(jsonString);
            for (String key : document.keySet()) {
                Object value = document.get(key);
                if (value instanceof Document) {
                    this.data.put(key, new DataDocument((Document) value));
                } else {
                    this.data.put(key, value);
                }
            }
        } catch (JsonParseException e) {
            ServerUtils.error("Invalid JSON string: " + jsonString);
            throw e;
        }
    }

    public Document toDocument() {
        Document document = new Document();
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof DataDocument) {
                document.put(entry.getKey(), ((DataDocument) entry.getValue()).toDocument());
            } else {
                document.put(entry.getKey(), entry.getValue());
            }
        }
        return document;
    }

    public int getInteger(String key, int defaultValue) {
        Object value = data.get(key);
        if (value == null) return defaultValue;

        return (int) value;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Object value = data.get(key);
        if (value == null) return defaultValue;

        return (boolean) value;
    }

    public String getString(String key, String defaultValue) {
        Object value = data.get(key);
        if (value == null) return defaultValue;

        return (String) value;
    }

    public String getString(String key) {
        Object value = data.get(key);
        return (String) value;
    }

    @SuppressWarnings("unchecked")
    public List<String> getList(String key, List<String> defaultValue) {
        Object value = data.get(key);
        if (value == null) return defaultValue;

        return (List<String>) value;
    }

    public DataDocument getDataDocument(String key) {
        DataDocument value = (DataDocument) data.get(key);
        if (value == null) {
            data.put(key, new DataDocument());
        }

        return (DataDocument) data.get(key);
    }

    public void put(String key, Object value) {
        this.data.put(key, value);
    }
}
