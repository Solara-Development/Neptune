package dev.lrxh.neptune.game.arena;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.api.arena.IArenaService;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import java.util.LinkedHashSet;

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
        loadAll("arenas", arenas, Arena::read);
    }

    @Override
    public void save() {
        saveAll("arenas", arenas, Arena::getName);
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
        return new Arena(arena.getName(), arena.getDisplayName(), arena.getRedSpawn(), arena.getBlueSpawn(), arena.getMin(), arena.getMax(), arena.getBuildLimit(), arena.isEnabled(), arena.getWhitelistedBlocks(), arena.getDeathY(), arena.getTime());
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getArenasConfig();
    }
}
