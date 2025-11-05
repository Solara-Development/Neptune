package dev.lrxh.neptune.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class WorldUtils {
    public Set<Player> getPlayersInRadius(Location center, double radius) {
        World world = center.getWorld();
        if (world == null) return new HashSet<>();

        Set<Player> nearbyEntities = new HashSet<>();
        int chunkRadius = (int) Math.ceil(radius / 16.0);

        int centerChunkX = center.getBlockX() >> 4;
        int centerChunkZ = center.getBlockZ() >> 4;

        for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
            for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                int chunkX = centerChunkX + dx;
                int chunkZ = centerChunkZ + dz;

                if (!world.isChunkLoaded(chunkX, chunkZ)) continue;

                for (Entity entity : world.getChunkAt(chunkX, chunkZ).getEntities()) {

                    if (entity instanceof Player player) {
                        if (entity.getLocation().distanceSquared(center) > radius * radius) continue;
                        nearbyEntities.add(player);
                    }
                }
            }
        }

        return nearbyEntities;
    }
}
