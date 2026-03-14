package dev.lrxh.api.features.cosmetics.armortrims;

import dev.lrxh.api.features.cosmetics.ICosmetic;

import java.util.Map;

public interface IArmorTrimCosmetic extends ICosmetic {
    Map<String, ? extends IArmorTrimPackage> getPackages();
    IArmorTrimPackage getDefault();
    IArmorTrimPackage getOrDefault(String packageName);
    void addPackage(IArmorTrimPackage cosmeticPackage);
}
