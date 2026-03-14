package dev.lrxh.neptune.game.arena;

import com.google.common.collect.Lists;
import dev.lrxh.api.arena.IArena;
import dev.lrxh.blockChanger.BlockChanger;
import dev.lrxh.blockChanger.snapshot.CuboidSnapshot;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.kit.KitService;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

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
    private long time;
    private List<Material> whitelistedBlocks;
    private CuboidSnapshot snapshot;
    private Arena owner;
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
                 List<Material> whitelistedBlocks, int deathY, long time, CuboidSnapshot snapshot, Arena owner) {

        this(name, displayName, redSpawn, blueSpawn, min, max, buildLimit, enabled, whitelistedBlocks, deathY, time);
        this.snapshot = snapshot;
        this.owner = owner;
        this.doneLoading = (snapshot != null);
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

    public synchronized CompletableFuture<VirtualArena> createDuplicate() {
        CompletableFuture<VirtualArena> future = new CompletableFuture<>();
        UUID uuid = UUID.randomUUID();
        WorldCreator creator = new WorldCreator(uuid.toString())
                .type(WorldType.NORMAL)
                .generator(new ChunkGenerator() {
                    @Override
                    public boolean canSpawn(@NotNull World world, int x, int z) {
                        return true;
                    }

                    @Override
                    public @NotNull Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
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
            try {
                World world = virtualWorld.getWorld();
                Bukkit.getScheduler().runTask(Neptune.get(), () -> {
                    world.setGameRule(GameRules.ADVANCE_TIME, false);
                    world.setGameRule(GameRules.ADVANCE_WEATHER, false);
                    world.setGameRule(GameRules.SHOW_ADVANCEMENT_MESSAGES, false);
                    world.setGameRule(GameRules.IMMEDIATE_RESPAWN, true);
                    world.setDifficulty(Difficulty.HARD);
                    world.setTime(time);
                });

                Location min = this.min.clone();
                min.setWorld(world);
                Location max = this.max.clone();
                max.setWorld(world);
                Location redSpawn = this.redSpawn.clone();
                redSpawn.setWorld(world);
                Location blueSpawn = this.blueSpawn.clone();
                blueSpawn.setWorld(world);


                virtualWorld.paste(snapshot);

                String dupName = this.name + "_" + uuid;

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
                        this.time,
                        this,
                        virtualWorld
                );

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
