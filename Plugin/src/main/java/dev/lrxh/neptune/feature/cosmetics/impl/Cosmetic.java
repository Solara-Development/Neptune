package dev.lrxh.neptune.feature.cosmetics.impl;

import dev.lrxh.api.features.cosmetics.ICosmetic;
import dev.lrxh.api.features.cosmetics.ICosmeticPackage;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Getter
public abstract class Cosmetic implements ICosmetic {
    public abstract String key();
    public abstract Map<String, ? extends CosmeticPackage> getPackages();
    public abstract ICosmeticPackage getOrDefault(String packageName);
    public Set<String> getKeys(FileConfiguration config, String path) {
        return Objects.requireNonNull(config.getConfigurationSection(path)).getKeys(false);
    }
    public abstract YamlConfiguration getConfig();
}
