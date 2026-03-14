package dev.lrxh.api.features.cosmetics;

import java.util.Map;

public interface ICosmetic {
    String key();
    Map<String, ? extends ICosmeticPackage> getPackages();
}
