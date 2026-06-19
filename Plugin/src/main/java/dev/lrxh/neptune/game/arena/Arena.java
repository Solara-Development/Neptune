package dev.lrxh.neptune.game.arena;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.providers.manager.ConfigData;
import dev.lrxh.neptune.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class Arena implements IArena, ConfigData {
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
    private Object faweClipboard;
    private Arena owner;
    private boolean doneLoading;
    private boolean inUse;
    private boolean allowedInCustomKit;

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
        this.allowedInCustomKit = true;
    }

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn,
                 Location min, Location max, double buildLimit, boolean enabled,
                 List<Material> whitelistedBlocks, int deathY, long time) {

        this(name, displayName, redSpawn, blueSpawn, enabled, deathY, time);
        this.min = min;
        this.max = max;
        this.buildLimit = buildLimit;
        this.whitelistedBlocks = (whitelistedBlocks != null ? whitelistedBlocks : new ArrayList<>());

        capture();
    }

    Arena(String name, String displayName, Location redSpawn, Location blueSpawn,
          Location min, Location max, double buildLimit, boolean enabled,
          List<Material> whitelistedBlocks, int deathY, long time, Arena owner) {

        this(name, displayName, redSpawn, blueSpawn, enabled, deathY, time);
        this.min = min;
        this.max = max;
        this.buildLimit = buildLimit;
        this.whitelistedBlocks = (whitelistedBlocks != null ? whitelistedBlocks : new ArrayList<>());
        this.owner = owner;
        this.doneLoading = true;
    }

    public Arena(String name, long time) {
        this(name, name, null, null, false, -68321, time);
        this.min = null;
        this.max = null;
        this.buildLimit = 68321;
        this.whitelistedBlocks = new ArrayList<>();
    }

    public static Arena read(String name, ConfigurationSection s) {
        if (!s.contains("displayName")) return null;
        List<Material> blocks = new ArrayList<>();
        for (String n : s.getStringList("whitelistedBlocks")) {
            Material m = Material.getMaterial(n);
            if (m != null) blocks.add(m);
        }
        Arena arena = new Arena(name, s.getString("displayName"),
                LocationUtil.deserialize(s.getString("redSpawn")),
                LocationUtil.deserialize(s.getString("blueSpawn")),
                LocationUtil.deserialize(s.getString("min")),
                LocationUtil.deserialize(s.getString("max")),
                s.getDouble("limit"), s.getBoolean("enabled"),
                blocks, s.getInt("deathY", -68321), s.getLong("time"));
        arena.setAllowedInCustomKit(s.getBoolean("allowedInCustomKit", true));
        return arena;
    }

    @Override
    public double getBuildLimit() {
        return owner != null ? owner.getBuildLimit() : buildLimit;
    }

    @Override
    public List<Material> getWhitelistedBlocks() {
        return owner != null ? owner.getWhitelistedBlocks() : whitelistedBlocks;
    }

    @Override
    public int getDeathY() {
        return owner != null ? owner.getDeathY() : deathY;
    }

    @Override
    public long getTime() {
        return owner != null ? owner.getTime() : time;
    }

    @Override
    public boolean isEnabled() {
        return owner != null ? owner.isEnabled() : enabled;
    }

    @Override
    public String getDisplayName() {
        return owner != null ? owner.getDisplayName() : displayName;
    }

    public boolean isAllowedInCustomKit() {
        return owner != null ? owner.isAllowedInCustomKit() : allowedInCustomKit;
    }

    @Override
    public boolean isSetup() {
        return !(redSpawn == null || blueSpawn == null || min == null || max == null);
    }

    @Override
    public void remove() {
        inUse = false;
    }

    public synchronized CompletableFuture<IArena> acquire() {
        if (Neptune.get().isDuplicatesEnabled()) {
            Arena duplicate = ArenaService.get().getFreeDuplicate(this);
            if (duplicate == null) return CompletableFuture.completedFuture(null);
            duplicate.setInUse(true);
            return CompletableFuture.completedFuture(duplicate);
        }
        if (inUse) return CompletableFuture.completedFuture(null);
        inUse = true;
        return CompletableFuture.completedFuture(this);
    }

    public List<String> getWhitelistedBlocksAsString() {
        List<String> result = new ArrayList<>();
        for (Material mat : whitelistedBlocks) {
            result.add(mat.name());
        }
        return result;
    }

    @Override
    public void write(ConfigurationSection s) {
        s.set("displayName", displayName);
        s.set("redSpawn", LocationUtil.serialize(redSpawn));
        s.set("blueSpawn", LocationUtil.serialize(blueSpawn));
        s.set("enabled", enabled);
        s.set("deathY", deathY);
        s.set("time", time);
        s.set("limit", buildLimit);
        s.set("whitelistedBlocks", getWhitelistedBlocksAsString());
        s.set("allowedInCustomKit", allowedInCustomKit);
        if (min != null) s.set("min", LocationUtil.serialize(min));
        if (max != null) s.set("max", LocationUtil.serialize(max));
        if (owner != null) s.set("owner", owner.getName());
    }

    public void restore() {
        if (owner != null && faweClipboard == null) {
            Arena source = owner;
            if (source.getMin() != null && source.getMin().getWorld() != null) {
                int tx = Math.min(min.getBlockX(), max.getBlockX());
                int ty = Math.min(min.getBlockY(), max.getBlockY());
                int tz = Math.min(min.getBlockZ(), max.getBlockZ());
                Bukkit.getScheduler().runTaskAsynchronously(Neptune.get(), () ->
                        ArenaDuplicator.copyPaste(source.getMin().getWorld(), source.getMin(), source.getMax(),
                                min.getWorld(), tx, ty, tz));
            }
            return;
        }
        if (faweClipboard != null) {
            Bukkit.getScheduler().runTaskAsynchronously(Neptune.get(),
                    () -> ArenaDuplicator.restore(min.getWorld(), faweClipboard));
        }
    }

    public void capture() {
        if (min == null || max == null) return;
        if (owner != null) {
            this.doneLoading = true;
            return;
        }
        this.doneLoading = false;
        Bukkit.getScheduler().runTaskAsynchronously(Neptune.get(), () -> {
            if (owner == null) {
                this.faweClipboard = ArenaDuplicator.capture(min.getWorld(), min, max);
            }
            this.doneLoading = true;
        });
    }

    public void setMin(Location min) {
        this.min = min;
        capture();
    }

    public void setMax(Location max) {
        this.max = max;
        capture();
    }

    public void setMaxDirect(Location max) {
        this.max = max;
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

    public void delete(boolean save) {
        KitService.get().removeArenasFromKits(this);
        ArenaService.get().arenas.remove(this);

        if (save) ArenaService.get().save();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Arena arena) {
            return arena.getName().equals(name);
        }
        return false;
    }
}
