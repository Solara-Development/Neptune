package dev.lrxh.neptune.game.kit;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.api.kit.IKit;
import dev.lrxh.api.kit.IKitService;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.providers.manager.Value;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.PotionEffectUtils;
import dev.lrxh.neptune.utils.ServerUtils;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

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
        FileConfiguration config = ConfigService.get().getKitsConfig().getConfiguration();
        if (config.contains("kits")) {
            for (String kitName : getKeys("kits")) {
                String path = "kits." + kitName + ".";
                String displayName = config.getString(path + "displayName", kitName);
                ItemStack icon = ItemUtils.deserializeItem(config.getString(path + "icon", ""));

                List<ItemStack> items = ItemUtils.deserialize(config.getString(path + "items", ""));
                int slot = config.getInt(path + "slot", kits.size() + 1);
                int kitEditorSlot = config.getInt(path + "kitEditor-slot", slot);
                double health = config.getDouble(path + "health", 20);
                double damageMultiplier = config.getDouble(path + "damage-multiplier", 1.0);

                HashSet<Arena> arenas = new HashSet<>();
                if (!config.getStringList(path + "arenas").isEmpty()) {
                    for (String arenaName : config.getStringList(path + "arenas")) {
                        Arena arena = ArenaService.get().getArenaByName(arenaName);
                        if (arena == null) {
                            ServerUtils.error("KitService: Arena " + arenaName + " not found for kit " + kitName);
                            continue;
                        }
                        arenas.add(arena);
                    }
                }

                HashMap<KitRule, Boolean> rules = new HashMap<>();
                for (KitRule kitRule : KitRule.values()) {
                    rules.put(kitRule, config.getBoolean(path + kitRule.getSaveName(), false));
                }

                List<PotionEffect> potionEffects = new ArrayList<>();
                if (!config.getStringList(path + "potionEffects").isEmpty()) {
                    for (String potion : config.getStringList(path + "potionEffects")) {
                        potionEffects.add(PotionEffectUtils.deserialize(potion));
                    }
                }

                kits.add(new Kit(kitName, displayName, items, arenas, icon, rules, slot, health, kitEditorSlot, potionEffects, damageMultiplier));
            }
        }
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
        getConfigFile().getConfiguration().getKeys(false).forEach(key -> getConfigFile().getConfiguration().set(key, null));
        kits.forEach(kit -> {
            String path = "kits." + kit.getName().replaceAll("\\s+", "") + ".";
            List<Value> values = new ArrayList<>();

            values.add(new Value("displayName", kit.getDisplayName()));
            values.add(new Value("items", ItemUtils.serialize(kit.getItems())));
            values.add(new Value("arenas", kit.getArenasAsString()));
            values.add(new Value("potionEffects", kit.getPotionsAsString()));
            values.add(new Value("icon", ItemUtils.serialize(kit.getIcon())));
            values.add(new Value("slot", kit.getSlot()));
            values.add(new Value("health", kit.getHealth()));
            values.add(new Value("kitEditor-slot", kit.getKitEditorSlot()));
            values.add(new Value("damage-multiplier", kit.getDamageMultiplier()));

            for (Map.Entry<KitRule, Boolean> kitRuleEntry : kit.getRules().entrySet()) {
                values.add(new Value(kitRuleEntry.getKey().getSaveName(), kit.is(kitRuleEntry.getKey())));
            }

            save(values, path);
        });
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
                kit.getPotionEffects(),
                kit.getDamageMultiplier()
        );
    }
}
