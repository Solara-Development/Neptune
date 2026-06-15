package dev.lrxh.neptune.feature.rankedloadout;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RankedLoadoutData {
    private static final Gson GSON = new Gson();

    private String activeId;
    private List<StoredLoadout> loadouts = new ArrayList<>();

    public static RankedLoadoutData deserialize(String json) {
        if (json == null || json.isEmpty()) return new RankedLoadoutData();
        try {
            RankedLoadoutData data = GSON.fromJson(json, RankedLoadoutData.class);
            if (data == null) return new RankedLoadoutData();
            if (data.loadouts == null) data.loadouts = new ArrayList<>();
            return data;
        } catch (Exception ignored) {
            return new RankedLoadoutData();
        }
    }

    public String serialize() {
        return GSON.toJson(this);
    }

    public RankedLoadout toRankedLoadout(StoredLoadout stored) {
        return RankedLoadout.fromStored(stored.id, stored.name, stored.items);
    }

    public List<RankedLoadout> toRankedLoadouts() {
        List<RankedLoadout> result = new ArrayList<>();
        for (StoredLoadout stored : loadouts) {
            result.add(toRankedLoadout(stored));
        }
        return result;
    }

    public void fromRankedLoadouts(List<RankedLoadout> rankedLoadouts) {
        loadouts = new ArrayList<>();
        for (RankedLoadout loadout : rankedLoadouts) {
            StoredLoadout stored = new StoredLoadout();
            stored.id = loadout.getId();
            stored.name = loadout.getDisplayName();
            stored.items = loadout.serializedItems();
            loadouts.add(stored);
        }
    }

    static class StoredLoadout {
        String id;
        String name;
        String items;
    }
}
