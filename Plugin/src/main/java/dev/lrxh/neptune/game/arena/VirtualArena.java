package dev.lrxh.neptune.game.arena;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.blockChanger.world.VirtualWorld;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.Objects;

@Getter
public class VirtualArena implements IArena {
    private final String name;
    private final String displayName;
    private final boolean enabled;
    private final int deathY;
    private final long time;
    private final double buildLimit;
    private final List<Material> whitelistedBlocks;
    private final IArena owner;
    private final VirtualWorld virtualWorld;
    @Setter
    private Location redSpawn;
    @Setter
    private Location blueSpawn;
    @Setter
    private Location min;
    @Setter
    private Location max;

    public VirtualArena(String name,
                        String displayName,
                        Location redSpawn,
                        Location blueSpawn,
                        Location min,
                        Location max,
                        double buildLimit,
                        boolean enabled,
                        List<Material> whitelistedBlocks,
                        int deathY,
                        long time,
                        IArena owner,
                        VirtualWorld virtualWorld) {
        this.name = name;
        this.displayName = displayName;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.min = min;
        this.max = max;
        this.buildLimit = buildLimit;
        this.enabled = enabled;
        this.whitelistedBlocks = whitelistedBlocks;
        this.deathY = deathY;
        this.time = time;
        this.owner = owner;
        this.virtualWorld = virtualWorld;
    }

    @Override
    public boolean isSetup() {
        return redSpawn != null && blueSpawn != null && min != null && max != null && virtualWorld != null;
    }

    @Override
    public void remove() {
        virtualWorld.unload();
    }

    @Override
    public void restore() {
        //TODO: Find a better way to restore virtual worlds other than creating a CuboidSnapshot
    }

    @Override
    public void delete(boolean save) {
        //Empty since you can't delete a virtual arena
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualArena that = (VirtualArena) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
