package dev.lrxh.neptune.game.arena;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.fastasyncworldedit.core.extent.clipboard.MemoryOptimizedClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public final class ArenaDuplicator {

    // Serializes FAWE operations: all arena duplicates share one world and FAWE's
    // per-world chunk cache is not safe under concurrent EditSessions (stray blocks).
    private static final Object FAWE_LOCK = new Object();

    public static boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null;
    }

    public static void copyPaste(World sourceWorld, Location min, Location max, World targetWorld, int tx, int ty, int tz) {
        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(sourceWorld),
                BlockVector3.at(min.getBlockX(), min.getBlockY(), min.getBlockZ()),
                BlockVector3.at(max.getBlockX(), max.getBlockY(), max.getBlockZ()));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region, new MemoryOptimizedClipboard(region));
        clipboard.setOrigin(region.getMinimumPoint());

        synchronized (FAWE_LOCK) {
            try (EditSession source = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(sourceWorld))) {
                ForwardExtentCopy copy = new ForwardExtentCopy(source, region, clipboard, region.getMinimumPoint());
                copy.setCopyingEntities(false);
                Operations.complete(copy);
            }

            try (EditSession target = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(targetWorld))) {
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
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region, new MemoryOptimizedClipboard(region));
        clipboard.setOrigin(region.getMinimumPoint());

        synchronized (FAWE_LOCK) {
            try (EditSession source = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
                ForwardExtentCopy copy = new ForwardExtentCopy(source, region, clipboard, region.getMinimumPoint());
                copy.setCopyingEntities(false);
                Operations.complete(copy);
            }
        }
        return clipboard;
    }

    public static void restore(World world, Object clipboard) {
        Clipboard clip = (Clipboard) clipboard;
        synchronized (FAWE_LOCK) {
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


    public static void restore(World world, Object clipboard, Location toMin) {
        Clipboard clip = (Clipboard) clipboard;
        synchronized (FAWE_LOCK) {
            try (EditSession target = WorldEdit.getInstance().newEditSessionBuilder()
                    .world(BukkitAdapter.adapt(world))
                    .changeSetNull()
                    .fastMode(true)
                    .checkMemory(false)
                    .limitUnlimited()
                    .build()) {
                Operations.complete(new ClipboardHolder(clip)
                        .createPaste(target)
                        .to(BlockVector3.at(toMin.getBlockX(), toMin.getBlockY(), toMin.getBlockZ()))
                        .ignoreAirBlocks(false)
                        .copyEntities(false)
                        .build());
            }
        }
    }
}
