package dev.lrxh.neptune.game.arena;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.blockChanger.snapshot.CuboidSnapshot;
import dev.lrxh.neptune.game.kit.KitService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class Arena implements IArena {
    private String name;
    private String displayName;
    private Location redSpawn;
    private Location blueSpawn;
    private boolean enabled;
    private int deathY;
    private Location min;
    private Location max;
    private double buildLimit;
    private long time;
    private List<Material> whitelistedBlocks;
    private CuboidSnapshot snapshot;

    private final List<Arena> instances = new ArrayList<>();

    private volatile boolean playing = false;
    private boolean doneLoading;

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn, boolean enabled, int deathY, long time) {
        this.name = name;
        this.displayName = displayName;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.enabled = enabled;
        this.deathY = deathY;
        this.time = time;

        this.buildLimit = 0;
        this.whitelistedBlocks = new ArrayList<>();
        this.doneLoading = true;
    }

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn,
                 Location min, Location max, double buildLimit, boolean enabled,
                 List<Material> whitelistedBlocks, int deathY, long time) {

        this(name, displayName, redSpawn, blueSpawn, enabled, deathY, time);
        this.min = min;
        this.max = max;
        this.buildLimit = buildLimit;
        this.whitelistedBlocks = (whitelistedBlocks != null ? whitelistedBlocks : new ArrayList<>());

        this.doneLoading = true;
    }

    public Arena(String name, long time) {
        this(name, name, null, null, false, -68321, time);
        this.min = null;
        this.max = null;
        this.buildLimit = 68321;
        this.whitelistedBlocks = new ArrayList<>();
    }

    @Override
    public boolean isSetup() {
        return !(redSpawn == null || blueSpawn == null || min == null || max == null);
    }

    @Override
    public void remove() {
    }

    public List<String> getWhitelistedBlocksAsString() {
        List<String> result = new ArrayList<>();
        for (Material mat : whitelistedBlocks) {
            result.add(mat.name());
        }
        return result;
    }

    public void restore() {
        if (snapshot != null) {
            snapshot.restore(true);
        }
        this.playing = false;
    }

    public void setMin(Location min) {
        this.min = min;
    }

    public void setMax(Location max) {
        this.max = max;
    }

    public void captureSnapshot() {
        if (min != null && max != null) {
            CuboidSnapshot.create(min, max).thenAccept(cuboidSnapshot -> this.snapshot = cuboidSnapshot);
        }
    }

    public void setRedSpawn(Location redSpawn) {
        this.redSpawn = redSpawn;
        if (buildLimit == 68321) {
            this.buildLimit = redSpawn.getBlockY() + 5;
        }
    }

    public void setBlueSpawn(Location blueSpawn) {
        this.blueSpawn = blueSpawn;
        if (buildLimit == 68321) {
            this.buildLimit = blueSpawn.getBlockY() + 5;
        }
    }

    public void clearStructure() {
        if (min == null || max == null || min.getWorld() == null) return;

        World world = min.getWorld();
        int minX = Math.min(min.getBlockX(), max.getBlockX());
        int minY = Math.min(min.getBlockY(), max.getBlockY());
        int minZ = Math.min(min.getBlockZ(), max.getBlockZ());

        int maxX = Math.max(min.getBlockX(), max.getBlockX());
        int maxY = Math.max(min.getBlockY(), max.getBlockY());
        int maxZ = Math.max(min.getBlockZ(), max.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (!world.getBlockAt(x, y, z).getType().isAir()) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                    }
                }
            }
        }
    }

    @Override
    public void delete(boolean save) {
        delete(save, false);
    }

    public void delete(boolean save, boolean removeStructure) {
        for (Arena instance : new ArrayList<>(instances)) {
            instance.delete(false, removeStructure);
        }
        instances.clear();

        if (removeStructure) {
            clearStructure();
        }

        KitService.get().removeArenasFromKits(this);

        if (ArenaService.get().arenas.contains(this)) {
            ArenaService.get().arenas.remove(this);
        } else {
            for (Arena parent : ArenaService.get().arenas) {
                if (parent.getInstances().contains(this)) {
                    parent.getInstances().remove(this);
                    break;
                }
            }
        }

        if (save) ArenaService.get().save();
    }

    /**
     * Returns an available instance. Only copied instances are used; the original (parent) arena is never returned.
     */
    public Arena getAvailableArena() {
        if (instances.isEmpty()) {
            return null;
        }
        for (Arena instance : instances) {
            if (instance.isEnabled() && instance.isSetup() && instance.isDoneLoading() && !instance.isPlaying()) {
                if (instance.getSnapshot() == null) {
                    instance.captureSnapshot();
                }
                return instance;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Arena arena) {
            return arena.getName().equals(name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
