package dev.lrxh.neptune.game.arena;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.api.arena.IArenaService;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Getter
public class ArenaService extends IService implements IArenaService {
    private static ArenaService instance;
    public final LinkedHashSet<Arena> arenas = new LinkedHashSet<>();
    public final LinkedHashSet<Arena> duplicates = new LinkedHashSet<>();
    private final Set<Integer> reservedIndices = new HashSet<>();
    private static final int GRID_COLUMNS = 32;

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
        if (Neptune.get().isDuplicatesEnabled()) saveDuplicates();
    }

    public void loadDuplicates() {
        loadAll("duplicates", duplicates, this::readDuplicate);
    }

    public void saveDuplicates() {
        saveAll("duplicates", duplicates, Arena::getName);
    }

    private Arena readDuplicate(String name, ConfigurationSection s) {
        Arena dup = Arena.read(name, s);
        if (dup != null) dup.setOwner(getArenaByName(s.getString("owner")));
        return dup;
    }

    public List<Arena> getDuplicates(IArena owner) {
        List<Arena> result = new ArrayList<>();
        for (Arena dup : duplicates) {
            if (dup.getOwner() != null && dup.getOwner().getName().equalsIgnoreCase(owner.getName())) result.add(dup);
        }
        return result;
    }

    public Arena getFreeDuplicate(IArena owner) {
        for (Arena dup : duplicates) {
            if (dup.getOwner() != null && dup.getOwner().getName().equalsIgnoreCase(owner.getName())
                    && !dup.isInUse() && dup.isDoneLoading() && dup.isSetup()) {
                return dup;
            }
        }
        return null;
    }

    public int gridDistance() {
        return SettingsLocale.DUPLICATE_DISTANCE.getInt();
    }

    public int gridCellX(int index) {
        return (index % GRID_COLUMNS) * gridDistance();
    }

    public int gridCellZ(int index) {
        return (index / GRID_COLUMNS) * gridDistance();
    }

    public int nextFreeGridIndex() {
        Set<Integer> used = new HashSet<>();
        for (Arena dup : duplicates) {
            if (dup.getMin() != null) {
                int col = Math.floorDiv(dup.getMin().getBlockX(), gridDistance());
                int row = Math.floorDiv(dup.getMin().getBlockZ(), gridDistance());
                used.add(row * GRID_COLUMNS + col);
            }
        }
        int i = 0;
        while (used.contains(i) || reservedIndices.contains(i)) i++;
        return i;
    }

    private Location shift(Location loc, int dx, int dz, World world) {
        return new Location(world, loc.getX() + dx, loc.getY(), loc.getZ() + dz, loc.getYaw(), loc.getPitch());
    }

    public CompletableFuture<Arena> createDuplicate(Arena owner) {
        CompletableFuture<Arena> future = new CompletableFuture<>();
        World world = setupDuplicatesWorld();
        if (!owner.isSetup() || world == null) {
            future.complete(null);
            return future;
        }

        int index = nextFreeGridIndex();
        reservedIndices.add(index);
        int tx = gridCellX(index);
        int tz = gridCellZ(index);
        int ty = Math.min(owner.getMin().getBlockY(), owner.getMax().getBlockY());
        int dx = tx - Math.min(owner.getMin().getBlockX(), owner.getMax().getBlockX());
        int dz = tz - Math.min(owner.getMin().getBlockZ(), owner.getMax().getBlockZ());
        World ownerWorld = owner.getMin().getWorld();

        Bukkit.getScheduler().runTaskAsynchronously(Neptune.get(), () -> {
            try {
                ArenaDuplicator.copyPaste(ownerWorld, owner.getMin(), owner.getMax(), world, tx, ty, tz);
                Bukkit.getScheduler().runTask(Neptune.get(), () -> {
                    reservedIndices.remove(index);
                    Arena dup = new Arena(owner.getName() + "#" + index, owner.getDisplayName(),
                            shift(owner.getRedSpawn(), dx, dz, world), shift(owner.getBlueSpawn(), dx, dz, world),
                            shift(owner.getMin(), dx, dz, world), shift(owner.getMax(), dx, dz, world),
                            owner.getBuildLimit(), owner.isEnabled(), new ArrayList<>(owner.getWhitelistedBlocks()),
                            owner.getDeathY(), owner.getTime());
                    dup.setOwner(owner);
                    duplicates.add(dup);
                    save();
                    future.complete(dup);
                });
            } catch (Throwable t) {
                Bukkit.getScheduler().runTask(Neptune.get(), () -> reservedIndices.remove(index));
                future.completeExceptionally(t);
            }
        });
        return future;
    }

    public void recopyDuplicates(Arena owner) {
        World world = setupDuplicatesWorld();
        if (!owner.isSetup() || world == null) return;
        List<Arena> dups = new ArrayList<>();
        for (Arena dup : getDuplicates(owner)) {
            if (!dup.isInUse() && dup.isSetup()) dups.add(dup);
        }
        if (dups.isEmpty()) return;

        Bukkit.getScheduler().runTaskAsynchronously(Neptune.get(), () -> {
            for (Arena dup : dups) {
                ArenaDuplicator.copyPaste(owner.getMin().getWorld(), owner.getMin(), owner.getMax(), world,
                        Math.min(dup.getMin().getBlockX(), dup.getMax().getBlockX()),
                        Math.min(dup.getMin().getBlockY(), dup.getMax().getBlockY()),
                        Math.min(dup.getMin().getBlockZ(), dup.getMax().getBlockZ()));
            }
            Bukkit.getScheduler().runTask(Neptune.get(), () -> {
                for (Arena dup : dups) {
                    dup.capture();
                }
            });
        });
    }

    public void recopyAll() {
        for (Arena owner : arenas) recopyDuplicates(owner);
    }

    public void removeDuplicate(Arena duplicate) {
        duplicates.remove(duplicate);
        saveDuplicates();
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

    public World setupDuplicatesWorld() {
        String name = SettingsLocale.DUPLICATE_WORLD.getString();
        World world = Bukkit.getWorld(name);
        if (world == null) {
            world = new WorldCreator(name)
                    .type(WorldType.NORMAL)
                    .generator(new VoidChunkGenerator())
                    .biomeProvider(new VoidBiomeProvider())
                    .createWorld();
        }
        if (world != null) {
            world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
            world.setGameRule(GameRules.ADVANCE_WEATHER, false);
            world.setGameRule(GameRules.ADVANCE_TIME, false);
            world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
            world.setDifficulty(Difficulty.HARD);
            world.setAutoSave(true);
        }
        return world;
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getArenasConfig();
    }
}
