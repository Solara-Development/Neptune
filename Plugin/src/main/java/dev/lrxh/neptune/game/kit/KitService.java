package dev.lrxh.neptune.game.kit;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.api.kit.IKit;
import dev.lrxh.api.kit.IKitService;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import java.util.*;

@Getter
public class KitService extends IService implements IKitService {
    private static KitService instance;
    public final LinkedHashSet<Kit> kits = new LinkedHashSet<>();

    public static KitService get() {
        if (instance == null) instance = new KitService();

        return instance;
    }

    @Override
    public void load() {
        loadAll("kits", kits, Kit::read);
    }

    public boolean add(Kit kit) {
        for (Kit k : kits) {
            if (k.equals(kit)) return true;
        }
        kits.add(kit);
        return false;
    }

    public boolean addKit(IKit kit) {
        return add((Kit) kit);
    }

    @Override
    public void save() {
        saveAll("kits", kits, k -> k.getName().replaceAll("\\s+", ""));
    }


    public Kit getKitByName(String kitName) {
        for (Kit kit : kits) {
            if (kit.getName().equalsIgnoreCase(kitName)) {
                return kit;
            }
        }
        return null;
    }

    public Kit getKitByDisplay(String kitName) {
        for (Kit kit : kits) {
            if (kit.getDisplayName().equals(kitName)) {
                return kit;
            }
        }
        return null;
    }

    public List<String> getKitNames() {
        List<String> names = new ArrayList<>();
        for (Kit kit : kits) {
            names.add(kit.getName());
        }

        return names;
    }


    public void removeArenasFromKits(Arena arena) {
        for (Kit kit : kits) {
            kit.getArenas().remove(arena);
        }
    }

    public void removeArena(IArena arena) {
        removeArenasFromKits((Arena) arena);
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getKitsConfig();
    }

    public LinkedHashSet<IKit> getAllKits() {
        return new LinkedHashSet<>(kits);
    }

    public Kit copyFrom(IKit kit) {
        return new Kit(
                kit.getName(),
                kit.getDisplayName(),
                kit.getItems(),
                kit.getAllArenas().stream().map(ArenaService.get()::copyFrom).collect(HashSet::new, HashSet::add, HashSet::addAll),
                kit.getIcon(),
                kit.getRule().entrySet().stream().collect(HashMap::new,
                        (map, entry) -> map.put((KitRule) entry.getKey(), entry.getValue()), HashMap::putAll),
                kit.getSlot(),
                kit.getHealth(),
                kit.getKitEditorSlot(),
                kit.getLeaderboardSlot(),
                kit.getPotionEffects(),
                kit.getDamageMultiplier()
        );
    }
}
