package dev.lrxh.neptune.game.arena;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.blockChanger.snapshot.CuboidSnapshot;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.game.arena.allocator.Allocation;
import dev.lrxh.neptune.game.arena.allocator.SpatialAllocator;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

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
    private List<Material> whitelistedBlocks;
    private CuboidSnapshot snapshot;
    private AtomicInteger duplicateIndex;
    private Arena owner;
    private boolean doneLoading;
    private boolean used;

    private Long allocationId;

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn, boolean enabled, int deathY) {
        this.name = name;
        this.displayName = displayName;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.enabled = enabled;
        this.deathY = deathY;

        this.buildLimit = 0;
        this.whitelistedBlocks = new ArrayList<>();
        this.duplicateIndex = new AtomicInteger(1);
        this.doneLoading = false;
    }

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn,
                 Location min, Location max, double buildLimit, boolean enabled,
                 List<Material> whitelistedBlocks, int deathY) {

        this(name, displayName, redSpawn, blueSpawn, enabled, deathY);
        this.min = min;
        this.max = max;
        this.buildLimit = buildLimit;
        this.whitelistedBlocks = (whitelistedBlocks != null ? whitelistedBlocks : new ArrayList<>());

        if (min != null && max != null) {
            this.doneLoading = false;
            CuboidSnapshot.create(min, max).thenAccept(cuboidSnapshot -> {
                this.snapshot = cuboidSnapshot;
                this.doneLoading = true;
            });
        }
    }

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn,
                 Location min, Location max, double buildLimit, boolean enabled,
                 List<Material> whitelistedBlocks, int deathY, CuboidSnapshot snapshot, Arena owner, Long allocationId) {

        this(name, displayName, redSpawn, blueSpawn, min, max, buildLimit, enabled, whitelistedBlocks, deathY);
        this.snapshot = snapshot;
        this.owner = owner;
        this.doneLoading = (snapshot != null);
        this.allocationId = allocationId;
    }

    public Arena(String name) {
        this(name, name, null, null, false, -68321);
        this.min = null;
        this.max = null;
        this.buildLimit = 68321;
        this.whitelistedBlocks = new ArrayList<>();
        this.duplicateIndex = new AtomicInteger(1);
    }

    @Override
    public boolean isSetup() {
        return !(redSpawn == null || blueSpawn == null || min == null || max == null);
    }

    public synchronized CompletableFuture<Arena> createDuplicate() {

        if (snapshot == null) {
            CompletableFuture<Arena> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalStateException("CuboidSnapshot not ready"));
            return failed;
        }

        AtomicInteger indexCounter = (this.owner != null ? this.owner.duplicateIndex : this.duplicateIndex);
        int currentIndex = indexCounter.getAndIncrement();

        int arenaWidthBlocks = Math.abs(max.getBlockX() - min.getBlockX()) + 1;
        int arenaDepthBlocks = Math.abs(max.getBlockZ() - min.getBlockZ()) + 1;

        int widthWithOffsetBlocks = arenaWidthBlocks + SettingsLocale.ARENA_COPY_OFFSET_X.getInt();
        int depthWithOffsetBlocks = arenaDepthBlocks + SettingsLocale.ARENA_COPY_OFFSET_Z.getInt();

        int widthChunks = (widthWithOffsetBlocks + 15) / 16;
        int depthChunks = (depthWithOffsetBlocks + 15) / 16;

        Allocation allocation;
        try {
            allocation = SpatialAllocator.get().allocate(widthChunks, depthChunks);
        } catch (Exception ex) {
            CompletableFuture<Arena> failed = new CompletableFuture<>();
            failed.completeExceptionally(new IllegalStateException("Unable to reserve space for arena duplicate", ex));
            return failed;
        }

        final int originalMinChunkX = min.getBlockX() >> 4;
        final int originalMinChunkZ = min.getBlockZ() >> 4;
        final int chunkOffsetX = allocation.chunkX - originalMinChunkX;
        final int chunkOffsetZ = allocation.chunkZ - originalMinChunkZ;
        final int offsetBlocksX = chunkOffsetX * 16;
        final int offsetBlocksZ = chunkOffsetZ * 16;

        Location newRedSpawn = (this.redSpawn != null ? LocationUtil.addOffset(this.redSpawn.clone(), offsetBlocksX, offsetBlocksZ) : null);
        Location newBlueSpawn = (this.blueSpawn != null ? LocationUtil.addOffset(this.blueSpawn.clone(), offsetBlocksX, offsetBlocksZ) : null);
        Location newMin = LocationUtil.addOffset(this.min.clone(), offsetBlocksX, offsetBlocksZ);
        Location newMax = LocationUtil.addOffset(this.max.clone(), offsetBlocksX, offsetBlocksZ);

        CompletableFuture<Arena> future = new CompletableFuture<>();

        snapshot.offset(offsetBlocksX, offsetBlocksZ)
                .thenApplyAsync(cuboidSnapshot -> {
                    cuboidSnapshot.restore(false);

                    return new Arena(
                            this.name + "#" + currentIndex,
                            this.displayName,
                            newRedSpawn,
                            newBlueSpawn,
                            newMin,
                            newMax,
                            buildLimit,
                            enabled,
                            whitelistedBlocks,
                            deathY,
                            cuboidSnapshot,
                            this,
                            allocation.id
                    );
                }).whenComplete((arena, throwable) -> {
                    if (throwable != null) {
                        SpatialAllocator.get().free(allocation.id);
                        future.completeExceptionally(throwable);
                    } else {
                        future.complete(arena);
                    }

                });

        return future;
    }


    public List<String> getWhitelistedBlocksAsString() {
        List<String> result = new ArrayList<>();
        for (Material mat : whitelistedBlocks) {
            result.add(mat.name());
        }
        return result;
    }

    public void remove() {
        if (SettingsLocale.ARENA_DUPLICATES.getBoolean()) {
            used = false;
            return;
        }

        if (allocationId != null) {
            try {
                SpatialAllocator.get().free(allocationId);
            } catch (Exception ignored) {
            }
            allocationId = null;
        }

        if (owner != null) {
            owner.duplicateIndex.getAndDecrement();
        }
    }

    public void restore() {
        if (snapshot != null) {
            snapshot.restore(true);
        }
    }

    public void setMin(Location min) {
        this.min = min;
        if (min != null && max != null) {
            this.doneLoading = false;
            CuboidSnapshot.create(min, max).thenAccept(cuboidSnapshot -> {
                this.snapshot = cuboidSnapshot;
                this.doneLoading = true;
            });
        }
    }

    public void setMax(Location max) {
        this.max = max;
        if (min != null && max != null) {
            this.doneLoading = false;
            CuboidSnapshot.create(min, max).thenAccept(cuboidSnapshot -> {
                this.snapshot = cuboidSnapshot;
                this.doneLoading = true;
            });
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

    public void delete(boolean save) {
        KitService.get().removeArenasFromKits(this);
        ArenaService.get().arenas.remove(this);

        if (allocationId != null) {
            SpatialAllocator.get().free(allocationId);
            allocationId = null;
        }

        if (save) {
            ArenaService.get().save();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Arena arena) {
            return arena.getName().equals(name);
        }
        return false;
    }
}
