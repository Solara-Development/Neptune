package dev.lrxh.api.features.cosmetics.shieldpatterns;

import dev.lrxh.api.features.cosmetics.ICosmetic;

import java.util.Map;

public interface IShieldPatternCosmetic extends ICosmetic {
    Map<String, ? extends IShieldPatternPackage> getPackages();
    IShieldPatternPackage getDefault();
    IShieldPatternPackage getOrDefault(String packageName);
    void addPackage(IShieldPatternPackage killMessagePackage);
}
