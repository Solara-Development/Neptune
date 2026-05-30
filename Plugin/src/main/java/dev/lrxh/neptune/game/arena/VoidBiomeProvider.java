package dev.lrxh.neptune.game.arena;

import com.google.common.collect.Lists;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VoidBiomeProvider extends BiomeProvider {
    @Override
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
        return Biome.PLAINS;
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
        return Lists.newArrayList(Biome.PLAINS);
    }
}
