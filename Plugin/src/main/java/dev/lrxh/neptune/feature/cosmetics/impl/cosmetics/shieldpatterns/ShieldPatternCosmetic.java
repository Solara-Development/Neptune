package dev.lrxh.neptune.feature.cosmetics.impl.cosmetics.shieldpatterns;

import dev.lrxh.api.features.cosmetics.shieldpatterns.IShieldPatternCosmetic;
import dev.lrxh.api.features.cosmetics.shieldpatterns.IShieldPatternPackage;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.feature.cosmetics.CosmeticService;
import dev.lrxh.neptune.feature.cosmetics.impl.Cosmetic;
import dev.lrxh.neptune.utils.ConfigFile;
import dev.lrxh.neptune.utils.ItemUtils;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import lombok.Getter;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

@Getter
public class ShieldPatternCosmetic extends Cosmetic implements IShieldPatternCosmetic {
    private static ShieldPatternCosmetic instance;
    public static ShieldPatternCosmetic get() {
        if (instance == null) instance = new ShieldPatternCosmetic();
        return instance;
    }

    public String key() {
        return "shieldpatterns";
    }

    public Map<String, ShieldPatternPackage> packages = new HashMap<>();
    public void load() {
        packages.clear();
        FileConfiguration shieldPatternsConfig = getConfig();
        if (shieldPatternsConfig.contains("SHIELD_PATTERNS")) {
            for (String packageName : getKeys(shieldPatternsConfig, "SHIELD_PATTERNS")) {
                String path = "SHIELD_PATTERNS." + packageName + ".";
                String displayName = shieldPatternsConfig.getString(path + "DISPLAY_NAME");
                Material material = ItemUtils.getMaterial(Objects.requireNonNull(shieldPatternsConfig.getString(path + "MATERIAL")));
                List<String> description = shieldPatternsConfig.getStringList(path + "DESCRIPTION");
                int slot = shieldPatternsConfig.getInt(path + "SLOT");
                List<Pattern> patterns = new ArrayList<>();
                List<?> configPatterns = shieldPatternsConfig.getList(path + "PATTERNS");
                if (configPatterns != null) {
                    for (Object pattern : configPatterns) {
                        Map<String, String> map = (Map<String, String>) pattern;
                        patterns.add(new Pattern(DyeColor.valueOf(map.get("DYE")),
                                Objects.requireNonNull(RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN)
                                        .get(Objects.requireNonNull(NamespacedKey.fromString((map.get("PATTERN"))))))));
                    }
                }

                packages.put(packageName, new ShieldPatternPackage(packageName, displayName, material, description, slot, patterns));
            }
        }
        CosmeticService.get().registerCosmetic(this);
    }

    public ShieldPatternPackage getDefault() {
        return packages.get("DEFAULT");
    }
    public ShieldPatternPackage getOrDefault(String packageName) {
        if (packages.containsKey(packageName)) return packages.get(packageName);
        return getDefault();
    }

    public void addPackage(IShieldPatternPackage shieldPatternPackage) {
        packages.put(shieldPatternPackage.getName(), (ShieldPatternPackage) shieldPatternPackage);
    }
    public YamlConfiguration getConfig() {
        return getConfigFile().getConfiguration();
    }
    public ConfigFile getConfigFile() {
        return ConfigService.get().getShieldPatternsConfig();
    }
}
