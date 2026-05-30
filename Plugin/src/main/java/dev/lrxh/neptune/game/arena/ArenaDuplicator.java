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

/**
 * All FastAsyncWorldEdit usage is isolated here so the rest of the plugin stays loadable when FAWE is absent.
 * Must be invoked off the main thread; EditSessions are closed (try-with-resources) to flush changes.
 */
public final class ArenaDuplicator {

    /**
     * FAWE corrupts chunk data when multiple EditSessions edit a world concurrently. Batch duplicate creation
     * spawns one async task per duplicate, so all FAWE work is serialized through this lock to prevent the
     * "random blocks" corruption.
     */
    private static final Object FAWE_LOCK = new Object();

    public static boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit") != null;
    }

    public static void copyPaste(World sourceWorld, Location min, Location max, World targetWorld, int tx, int ty, int tz) {
        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(sourceWorld),
                BlockVector3.at(min.getBlockX(), min.getBlockY(), min.getBlockZ()),
                BlockVector3.at(max.getBlockX(), max.getBlockY(), max.getBlockZ()));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
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

    /**
     * Snapshots [min, max] into an in-memory clipboard for later cleanup. Returned as Object so callers stay FAWE-free.
     */
    public static Object capture(World world, Location min, Location max) {
        CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(world),
                BlockVector3.at(min.getBlockX(), min.getBlockY(), min.getBlockZ()),
                BlockVector3.at(max.getBlockX(), max.getBlockY(), max.getBlockZ()));
        BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
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

    /**
     * Pastes a clipboard from {@link #capture} back to its original location, resetting the arena.
     */
    public static void restore(World world, Object clipboard) {
        Clipboard clip = (Clipboard) clipboard;
        synchronized (FAWE_LOCK) {
            try (EditSession target = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
                Operations.complete(new ClipboardHolder(clip)
                        .createPaste(target)
                        .to(clip.getOrigin())
                        .ignoreAirBlocks(false)
                        .build());
            }
        }
    }
}
