package dev.lrxh.neptune.game.arena;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.api.arena.IArenaService;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.providers.manager.Value;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.LocationUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

@Getter
public class ArenaService extends IService implements IArenaService {
    private static ArenaService instance;
    public final LinkedHashSet<Arena> arenas = new LinkedHashSet<>();

    public static ArenaService get() {
        if (instance == null) instance = new ArenaService();

        return instance;
    }

    public LinkedHashSet<IArena> getAllArenas() {
        return arenas.stream().collect(LinkedHashSet::new, LinkedHashSet::add, LinkedHashSet::addAll);
    }

    @Override
    public void load() {
        FileConfiguration config = ConfigService.get().getArenasConfig().getConfiguration();
        if (config.contains("arenas")) {
            for (String arenaName : getKeys("arenas")) {
                Arena arena = loadArena(arenaName);
                arenas.add(arena);
            }
        }
    }

    public Arena loadArena(String arenaName) {
        FileConfiguration config = ConfigService.get().getArenasConfig().getConfiguration();
        String path = "arenas." + arenaName + ".";

        if (!config.contains(path + "displayName")) return null;

        String displayName = config.getString(path + "displayName");
        Location redSpawn = LocationUtil.deserialize(config.getString(path + "redSpawn"));
        Location blueSpawn = LocationUtil.deserialize(config.getString(path + "blueSpawn"));
        boolean enabled = config.getBoolean(path + "enabled");
        int deathY = config.getInt(path + "deathY", -68321);


        Location edge1 = LocationUtil.deserialize(config.getString(path + "min"));
        Location edge2 = LocationUtil.deserialize(config.getString(path + "max"));

        double limit = config.getDouble(path + "limit");
        List<Material> whitelistedBlocks = new ArrayList<>();

        for (String name : config.getStringList(path + "whitelistedBlocks")) {
            whitelistedBlocks.add(Material.getMaterial(name));
        }

        return new Arena(arenaName, displayName, redSpawn, blueSpawn, edge1, edge2, limit, enabled, whitelistedBlocks, deathY);
    }


    @Override
    public void save() {
        getConfigFile().getConfiguration().getKeys(false)
                .forEach(key -> getConfigFile().getConfiguration().set(key, null));

        arenas.forEach(arena -> {
            String path = "arenas." + arena.getName() + ".";

            List<Value> values = new ArrayList<>(Arrays.asList(
                    new Value("displayName", arena.getDisplayName()),
                    new Value("redSpawn", LocationUtil.serialize(arena.getRedSpawn())),
                    new Value("blueSpawn", LocationUtil.serialize(arena.getBlueSpawn())),
                    new Value("enabled", arena.isEnabled()),
                    new Value("deathY", arena.getDeathY()),
                    new Value("limit", arena.getBuildLimit()),
                    new Value("whitelistedBlocks", arena.getWhitelistedBlocksAsString())
            ));

            if (arena.getMin() != null) {
                values.add(new Value("min", LocationUtil.serialize(arena.getMin())));
            }

            if (arena.getMax() != null) {
                values.add(new Value("max", LocationUtil.serialize(arena.getMax())));
            }

            save(values, path);
        });
    }

    public Arena getArenaByName(String arenaName) {
        for (Arena arena : arenas) {
            if (arena != null && arena.getName() != null && arena.getName().equalsIgnoreCase(arenaName)) {
                return arena;
            }
        }
        return null;
    }

    public Arena copyFrom(IArena arena) {
        return new Arena(arena.getName(), arena.getDisplayName(), arena.getRedSpawn(), arena.getBlueSpawn(), arena.getMin(), arena.getMax(), arena.getBuildLimit(), arena.isEnabled(), arena.getWhitelistedBlocks(), arena.getDeathY());
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getArenasConfig();
    }
}
