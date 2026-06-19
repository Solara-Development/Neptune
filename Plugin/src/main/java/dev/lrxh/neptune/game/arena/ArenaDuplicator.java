package dev.lrxh.neptune.game.arena;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.concurrent.ConcurrentHashMap;

public final class ArenaDuplicator {

    // Per-world locks: FAWE's chunk cache is not safe under concurrent EditSessions
    // in the same world, but different worlds can safely run in parallel.
    private static final ConcurrentHashMap<World, Object> WORLD_LOCKS = new ConcurrentHashMap<>();

    private static Object getLock(World world) {
        return WORLD_LOCKS.computeIfAbsent(world, k -> new Object());
    }

    public static boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null;
    }

    public static void copyPaste(World sourceWorld, Location min, Location max, World targetWorld, int tx, int ty, int tz) {
        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(sourceWorld),
                BlockVector3.at(min.getBlockX(), min.getBlockY(), min.getBlockZ()),
                BlockVector3.at(max.getBlockX(), max.getBlockY(), max.getBlockZ()));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(region.getMinimumPoint());

        synchronized (getLock(targetWorld)) {
            try (EditSession source = WorldEdit.getInstance().newEditSessionBuilder()
                    .world(BukkitAdapter.adapt(sourceWorld))
                    .changeSetNull()
                    .fastMode(true)
                    .checkMemory(false)
                    .limitUnlimited()
                    .build()) {
                ForwardExtentCopy copy = new ForwardExtentCopy(source, region, clipboard, region.getMinimumPoint());
                copy.setCopyingEntities(false);
                Operations.complete(copy);
            }

            try (EditSession target = WorldEdit.getInstance().newEditSessionBuilder()
                    .world(BukkitAdapter.adapt(targetWorld))
                    .changeSetNull()
                    .fastMode(true)
                    .checkMemory(false)
                    .limitUnlimited()
                    .build()) {
                Operations.complete(new ClipboardHolder(clipboard)
                        .createPaste(target)
                        .to(BlockVector3.at(tx, ty, tz))
                        .ignoreAirBlocks(false)
                        .build());
            }
        }
    }

    public static Object capture(World world, Location min, Location max) {
        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(world),
                BlockVector3.at(min.getBlockX(), min.getBlockY(), min.getBlockZ()),
                BlockVector3.at(max.getBlockX(), max.getBlockY(), max.getBlockZ()));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
        clipboard.setOrigin(region.getMinimumPoint());

        synchronized (getLock(world)) {
            try (EditSession source = WorldEdit.getInstance().newEditSessionBuilder()
                    .world(BukkitAdapter.adapt(world))
                    .changeSetNull()
                    .fastMode(true)
                    .checkMemory(false)
                    .limitUnlimited()
                    .build()) {
                ForwardExtentCopy copy = new ForwardExtentCopy(source, region, clipboard, region.getMinimumPoint());
                copy.setCopyingEntities(false);
                Operations.complete(copy);
            }
        }
        return clipboard;
    }

    public static void restore(World world, Object clipboard) {
        Clipboard clip = (Clipboard) clipboard;
        synchronized (getLock(world)) {
            try (EditSession target = WorldEdit.getInstance().newEditSessionBuilder()
                    .world(BukkitAdapter.adapt(world))
                    .changeSetNull()
                    .fastMode(true)
                    .checkMemory(false)
                    .limitUnlimited()
                    .build()) {
                Operations.complete(new ClipboardHolder(clip)
                        .createPaste(target)
                        .to(clip.getOrigin())
                        .ignoreAirBlocks(false)
                        .copyEntities(false)
                        .build());
            }
        }
    }
}
