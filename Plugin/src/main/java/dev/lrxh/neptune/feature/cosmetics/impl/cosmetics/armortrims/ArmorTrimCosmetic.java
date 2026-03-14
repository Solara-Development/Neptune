package dev.lrxh.neptune.feature.cosmetics.impl.cosmetics.armortrims;

import dev.lrxh.api.features.cosmetics.armortrims.IArmorTrimCosmetic;
import dev.lrxh.api.features.cosmetics.armortrims.IArmorTrimPackage;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.feature.cosmetics.CosmeticService;
import dev.lrxh.neptune.feature.cosmetics.impl.Cosmetic;
import dev.lrxh.neptune.utils.ItemUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.trim.ArmorTrim;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class ArmorTrimCosmetic extends Cosmetic implements IArmorTrimCosmetic {
    private static ArmorTrimCosmetic instance;
    public static ArmorTrimCosmetic get() {
        if (instance == null) instance = new ArmorTrimCosmetic();
        return instance;
    }

    public String key() {
        return "armortrims";
    }

    private final Map<String, ArmorTrimPackage> packages = new HashMap<>();
    public void load() {
        packages.clear();
        FileConfiguration armorTrimsConfig = ConfigService.get().getArmorTrimsConfig().getConfiguration();
        if (!armorTrimsConfig.contains("ARMOR_TRIMS")) return;
        for (String armorTrimName : getKeys(armorTrimsConfig, "ARMOR_TRIMS")) {
            String path = "ARMOR_TRIMS." + armorTrimName + ".";
            String displayName = armorTrimsConfig.getString(path + "DISPLAY_NAME");
            Material material = ItemUtils.getMaterial(Objects.requireNonNull(armorTrimsConfig.getString(path + "MATERIAL")));
            List<String> description = armorTrimsConfig.getStringList(path + "DESCRIPTION");
            int slot = armorTrimsConfig.getInt(path + "SLOT");
            ArmorTrim helmetTrim = ItemUtils.getArmorTrim(armorTrimsConfig.getString(path + "HELMET.MATERIAL"), armorTrimsConfig.getString(path + "HELMET.PATTERN"));
            String helmetName = armorTrimsConfig.getString(path + "HELMET.DISPLAY_NAME");
            ArmorTrim chestplateTrim = ItemUtils.getArmorTrim(armorTrimsConfig.getString(path + "CHESTPLATE.MATERIAL"), armorTrimsConfig.getString(path + "CHESTPLATE.PATTERN"));
            String chestplateName = armorTrimsConfig.getString(path + "CHESTPLATE.DISPLAY_NAME");
            ArmorTrim leggingsTrim = ItemUtils.getArmorTrim(armorTrimsConfig.getString(path + "LEGGINGS.MATERIAL"), armorTrimsConfig.getString(path + "LEGGINGS.PATTERN"));
            String leggingsName = armorTrimsConfig.getString(path + "LEGGINGS.DISPLAY_NAME");
            ArmorTrim bootsTrim = ItemUtils.getArmorTrim(armorTrimsConfig.getString(path + "BOOTS.MATERIAL"), armorTrimsConfig.getString(path + "BOOTS.PATTERN"));
            String bootsName = armorTrimsConfig.getString(path + "BOOTS.DISPLAY_NAME");

            packages.put(armorTrimName, new ArmorTrimPackage(
                    armorTrimName, displayName, material, description, slot,
                    helmetTrim, helmetName,
                    chestplateTrim, chestplateName,
                    leggingsTrim, leggingsName,
                    bootsTrim, bootsName));
        }
        CosmeticService.get().registerCosmetic(this);
    }

    public ArmorTrimPackage getDefault() {
        return packages.get("DEFAULT");
    }
    public ArmorTrimPackage getOrDefault(String packageName) {
        if (packages.containsKey(packageName)) return packages.get(packageName);
        return getDefault();
    }

    public void addPackage(IArmorTrimPackage armorTrimPackage) {
        packages.put(armorTrimPackage.getName(), (ArmorTrimPackage) armorTrimPackage);
    }
    public YamlConfiguration getConfig() {
        return ConfigService.get().getArmorTrimsConfig().getConfiguration();
    }
}
