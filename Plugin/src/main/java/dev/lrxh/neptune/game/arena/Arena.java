package dev.lrxh.neptune.game.arena;

import com.google.common.collect.Lists;
import dev.lrxh.api.arena.IArena;
import dev.lrxh.blockChanger.BlockChanger;
import dev.lrxh.blockChanger.snapshot.CuboidSnapshot;
import dev.lrxh.neptune.game.kit.KitService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    private Arena owner;
    private boolean doneLoading;

    public Arena(String name, String displayName, Location redSpawn, Location blueSpawn, boolean enabled, int deathY) {
        this.name = name;
        this.displayName = displayName;
        this.redSpawn = redSpawn;
        this.blueSpawn = blueSpawn;
        this.enabled = enabled;
        this.deathY = deathY;

        this.buildLimit = 0;
        this.whitelistedBlocks = new ArrayList<>();
        this.doneLoading = true;
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
                 List<Material> whitelistedBlocks, int deathY, CuboidSnapshot snapshot, Arena owner) {

        this(name, displayName, redSpawn, blueSpawn, min, max, buildLimit, enabled, whitelistedBlocks, deathY);
        this.snapshot = snapshot;
        this.owner = owner;
        this.doneLoading = (snapshot != null);
    }

    public Arena(String name) {
        this(name, name, null, null, false, -68321);
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

    public synchronized CompletableFuture<VirtualArena> createDuplicate() {
        CompletableFuture<VirtualArena> future = new CompletableFuture<>();
        UUID uuid = UUID.randomUUID();
        long methodStart = System.currentTimeMillis();
        WorldCreator creator = new WorldCreator(uuid.toString())
                .type(WorldType.NORMAL)
                .generator(new ChunkGenerator() {
                    @Override
                    public boolean canSpawn(@NotNull World world, int x, int z) {
                        return true;
                    }

                    @Override
                    public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
                        return new ArrayList<>();
                    }

                    @Override
                    public @Nullable Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
                        return new Location(world, 0, 0, 0);
                    }


                    @Override
                    public boolean shouldGenerateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
                        return false;
                    }

                    @Override
                    public boolean shouldGenerateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
                        return false;
                    }

                    @Override
                    public boolean shouldGenerateBedrock() {
                        return false;
                    }

                    @Override
                    public boolean shouldGenerateCaves(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
                        return false;
                    }

                    @Override
                    public boolean shouldGenerateDecorations(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
                        return false;
                    }

                    @Override
                    public boolean shouldGenerateMobs(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
                        return false;
                    }

                    @Override
                    public boolean shouldGenerateStructures(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ) {
                        return false;
                    }
                })
                .biomeProvider(new BiomeProvider() {
                    @Override
                    public org.bukkit.block.@NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
                        return org.bukkit.block.Biome.PLAINS;
                    }

                    @Override
                    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
                        return Lists.newArrayList(org.bukkit.block.Biome.PLAINS);
                    }
                });

        BlockChanger.createVirtualWorld(creator).thenAccept(virtualWorld -> {
            long virtualWorldCreatedAt = System.currentTimeMillis();
            try {
                World world = virtualWorld.getWorld();
                long t1 = System.currentTimeMillis();

                Location min = this.min.clone();
                min.setWorld(world);
                Location max = this.max.clone();
                max.setWorld(world);
                Location redSpawn = this.redSpawn.clone();
                redSpawn.setWorld(world);
                Location blueSpawn = this.blueSpawn.clone();
                blueSpawn.setWorld(world);

                long t2 = System.currentTimeMillis();

                long pasteStart = System.currentTimeMillis();
                virtualWorld.paste(snapshot);

                long pasteEnd = System.currentTimeMillis();

                long idStart = System.currentTimeMillis();
                String dupName = this.name + "_" + uuid;
                long idEnd = System.currentTimeMillis();

                long createStart = System.currentTimeMillis();
                VirtualArena duplicate = new VirtualArena(
                        dupName,
                        this.displayName,
                        redSpawn,
                        blueSpawn,
                        min,
                        max,
                        this.buildLimit,
                        this.enabled,
                        new ArrayList<>(this.whitelistedBlocks),
                        this.deathY,
                        this,
                        virtualWorld
                );

                long createEnd = System.currentTimeMillis();

                long methodEnd = System.currentTimeMillis();
                Bukkit.getLogger().info("[Arena:" + name + "] createDuplicate() finished successfully in "
                        + (methodEnd - methodStart) + "ms total"
                        + " | breakdown: world=" + (virtualWorldCreatedAt - methodStart)
                        + "ms, clone=" + (t2 - t1)
                        + "ms, paste=" + (pasteEnd - pasteStart)
                        + "ms, namePrep=" + (idEnd - idStart)
                        + "ms, arenaCtor=" + (createEnd - createStart)
                        + "ms");

                future.complete(duplicate);

            } catch (Exception ex) {
                future.completeExceptionally(ex);
            }
        }).exceptionally(ex -> {
            future.completeExceptionally(ex);
            return null;
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
