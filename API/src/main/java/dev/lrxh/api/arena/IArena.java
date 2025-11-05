package dev.lrxh.api.arena;

import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;

public interface IArena {
    String getName();

    String getDisplayName();

    Location getRedSpawn();

    void setRedSpawn(Location redSpawn);

    Location getBlueSpawn();

    void setBlueSpawn(Location blueSpawn);

    boolean isEnabled();

    int getDeathY();

    Location getMin();

    void setMin(Location min);

    Location getMax();

    void setMax(Location max);

    double getBuildLimit();

    List<Material> getWhitelistedBlocks();

    boolean isSetup();

    void remove();

    void restore();

    void delete(boolean save);

    boolean equals(Object o);
}
