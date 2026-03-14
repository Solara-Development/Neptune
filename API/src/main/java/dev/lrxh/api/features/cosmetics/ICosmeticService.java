package dev.lrxh.api.features.cosmetics;

import dev.lrxh.api.features.cosmetics.armortrims.IArmorTrimPackage;
import dev.lrxh.api.features.cosmetics.killmessages.IKillMessagePackage;

import java.util.Map;

public interface ICosmeticService {
    void registerCosmetic(ICosmetic cosmetic);
    Map<String, ? extends ICosmeticPackage> getPackages(ICosmetic cosmetic);
    Map<String, ? extends IKillMessagePackage> getKillMessagePackages();
    Map<String, ? extends IArmorTrimPackage> getArmorTrimPackages();
}
