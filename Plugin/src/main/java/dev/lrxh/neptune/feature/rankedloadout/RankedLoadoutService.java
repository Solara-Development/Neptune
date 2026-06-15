package dev.lrxh.neptune.feature.rankedloadout;

import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.profile.impl.Profile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RankedLoadoutService {
    private static final String STORAGE_KEY = "rankedLoadouts";
    private static RankedLoadoutService instance;

    public static RankedLoadoutService get() {
        if (instance == null) instance = new RankedLoadoutService();
        return instance;
    }

    public RankedLoadoutData getData(Profile profile, Kit kit) {
        KitData kitData = profile.getGameData().get(kit);
        Object stored = kitData.getPersistentData(STORAGE_KEY);
        RankedLoadoutData data;
        if (stored instanceof String json) {
            data = RankedLoadoutData.deserialize(json);
        } else {
            data = new RankedLoadoutData();
        }

        if (data.getLoadouts().isEmpty()) {
            seedDefault(profile, kit, data);
        }
        sanitizeAll(profile, kit, data);
        return data;
    }

    private void seedDefault(Profile profile, Kit kit, RankedLoadoutData data) {
        RankedLoadout loadout = new RankedLoadout("loadout_1", "Loadout #1",
                LoadoutWhitelistUtils.sanitize(kit, new ArrayList<>(kit.getItems())));
        data.setActiveId(loadout.getId());
        data.fromRankedLoadouts(List.of(loadout));
        persist(profile, kit, data);
    }

    public void persist(Profile profile, Kit kit, RankedLoadoutData data) {
        profile.getGameData().get(kit).setPersistentData(STORAGE_KEY, data.serialize());
    }

    public List<RankedLoadout> getLoadouts(Profile profile, Kit kit) {
        return getData(profile, kit).toRankedLoadouts();
    }

    public RankedLoadout getLoadout(Profile profile, Kit kit, String id) {
        for (RankedLoadout loadout : getLoadouts(profile, kit)) {
            if (loadout.getId().equalsIgnoreCase(id)) return loadout;
        }
        return null;
    }

    public RankedLoadout getActiveLoadout(Profile profile, Kit kit) {
        RankedLoadoutData data = getData(profile, kit);
        if (data.getActiveId() == null) return getLoadouts(profile, kit).getFirst();
        RankedLoadout active = getLoadout(profile, kit, data.getActiveId());
        if (active == null && !getLoadouts(profile, kit).isEmpty()) {
            active = getLoadouts(profile, kit).getFirst();
            data.setActiveId(active.getId());
            persist(profile, kit, data);
        }
        return active;
    }

    public RankedLoadout createLoadout(Profile profile, Kit kit, Player player, String displayName) {
        RankedLoadoutData data = getData(profile, kit);
        int max = LoadoutPermissionUtils.getMaxLoadouts(player);
        if (data.getLoadouts().size() >= max) return null;

        String id = toId(displayName);
        int suffix = 1;
        while (getLoadout(profile, kit, id) != null) {
            id = toId(displayName) + "_" + suffix++;
        }

        RankedLoadout loadout = new RankedLoadout(id, displayName,
                LoadoutWhitelistUtils.sanitize(kit, new ArrayList<>(kit.getItems())));
        List<RankedLoadout> loadouts = data.toRankedLoadouts();
        loadouts.add(loadout);
        data.fromRankedLoadouts(loadouts);
        if (data.getActiveId() == null) data.setActiveId(id);
        persist(profile, kit, data);
        return loadout;
    }

    public boolean deleteLoadout(Profile profile, Kit kit, String id) {
        RankedLoadoutData data = getData(profile, kit);
        List<RankedLoadout> loadouts = data.toRankedLoadouts();
        RankedLoadout removed = loadouts.stream()
                .filter(l -> l.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
        if (removed == null || loadouts.size() <= 1) return false;

        loadouts.removeIf(l -> l.getId().equalsIgnoreCase(id));
        data.fromRankedLoadouts(loadouts);
        if (id.equalsIgnoreCase(data.getActiveId())) {
            data.setActiveId(loadouts.getFirst().getId());
        }
        persist(profile, kit, data);
        return true;
    }

    public void setActiveLoadout(Profile profile, Kit kit, String id) {
        if (getLoadout(profile, kit, id) == null) return;
        RankedLoadoutData data = getData(profile, kit);
        data.setActiveId(id);
        persist(profile, kit, data);
    }

    public void saveLoadout(Profile profile, Kit kit, RankedLoadout loadout) {
        RankedLoadoutData data = getData(profile, kit);
        loadout.setItems(LoadoutWhitelistUtils.sanitize(kit, loadout.getItems()));
        List<RankedLoadout> loadouts = data.toRankedLoadouts();
        for (int i = 0; i < loadouts.size(); i++) {
            if (loadouts.get(i).getId().equalsIgnoreCase(loadout.getId())) {
                loadouts.set(i, loadout);
                break;
            }
        }
        data.fromRankedLoadouts(loadouts);
        persist(profile, kit, data);
    }

    public void resetActiveLoadout(Profile profile, Kit kit) {
        RankedLoadout active = getActiveLoadout(profile, kit);
        if (active == null) return;
        active.setItems(LoadoutWhitelistUtils.sanitize(kit, new ArrayList<>(kit.getItems())));
        saveLoadout(profile, kit, active);
    }

    public List<ItemStack> resolveActiveLoadout(Profile profile, Kit kit) {
        RankedLoadout active = getActiveLoadout(profile, kit);
        if (active == null) return new ArrayList<>(kit.getItems());

        List<ItemStack> sanitized = LoadoutWhitelistUtils.sanitize(kit, active.getItems());
        if (isEmptyLoadout(sanitized)) return new ArrayList<>(kit.getItems());

        active.setItems(sanitized);
        saveLoadout(profile, kit, active);
        return sanitized;
    }

    public void sanitizeAll(Profile profile, Kit kit, RankedLoadoutData data) {
        List<RankedLoadout> loadouts = data.toRankedLoadouts();
        boolean changed = false;
        for (RankedLoadout loadout : loadouts) {
            List<ItemStack> sanitized = LoadoutWhitelistUtils.sanitize(kit, loadout.getItems());
            if (!sanitized.equals(loadout.getItems())) {
                loadout.setItems(sanitized);
                changed = true;
            }
        }
        if (changed) {
            data.fromRankedLoadouts(loadouts);
            persist(profile, kit, data);
        }
    }

    public void sanitizeOnProfileLoad(Profile profile, Kit kit) {
        if (!kit.is(dev.lrxh.neptune.game.kit.impl.KitRule.ADVANCED_KIT_EDITOR)) return;
        RankedLoadoutData data = getData(profile, kit);
        sanitizeAll(profile, kit, data);
    }

    private boolean isEmptyLoadout(List<ItemStack> items) {
        if (items == null) return true;
        for (ItemStack item : items) {
            if (item != null && !item.getType().isAir()) return false;
        }
        return true;
    }

    private String toId(String displayName) {
        String id = displayName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "_");
        if (id.isEmpty()) id = "loadout";
        return id;
    }
}
