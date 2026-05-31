package dev.lrxh.neptune.feature.customkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CustomKitService {
    public static int MAX_KITS = 5;
    private static CustomKitService instance;
    private final Map<UUID, List<CustomKit>> kits = new ConcurrentHashMap<>();

    public static CustomKitService get() {
        if (instance == null) instance = new CustomKitService();
        return instance;
    }

    public List<CustomKit> get(UUID uuid) {
        return kits.computeIfAbsent(uuid, k -> new ArrayList<>());
    }

    public CustomKit get(UUID uuid, String name) {
        for (CustomKit k : get(uuid)) if (k.getName().equalsIgnoreCase(name)) return k;
        return null;
    }

    public CustomKit create(UUID uuid, String displayName) {
        String id = displayName.toLowerCase().replaceAll("\\s+", "_");
        if (id.isEmpty() || get(uuid).size() >= MAX_KITS || get(uuid, id) != null) return null;
        CustomKit kit = new CustomKit(uuid, id, displayName);
        get(uuid).add(kit);
        return kit;
    }

    public void delete(UUID uuid, CustomKit kit) {
        get(uuid).remove(kit);
    }

    public List<String> serialize(UUID uuid) {
        List<String> out = new ArrayList<>();
        for (CustomKit k : get(uuid)) out.add(k.serialize());
        return out;
    }

    public void load(UUID uuid, List<String> data) {
        List<CustomKit> list = new ArrayList<>();
        if (data != null) for (String s : data) {
            try {
                list.add(CustomKit.deserialize(uuid, s));
            } catch (Exception ignored) {
            }
        }
        kits.put(uuid, list);
    }
}
