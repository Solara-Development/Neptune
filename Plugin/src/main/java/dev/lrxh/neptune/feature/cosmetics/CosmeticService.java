package dev.lrxh.neptune.feature.cosmetics;

import dev.lrxh.api.features.cosmetics.ICosmetic;
import dev.lrxh.api.features.cosmetics.ICosmeticService;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.feature.cosmetics.impl.Cosmetic;
import dev.lrxh.neptune.feature.cosmetics.impl.CosmeticPackage;
import dev.lrxh.neptune.feature.cosmetics.impl.armortrims.ArmorTrimCosmetic;
import dev.lrxh.neptune.feature.cosmetics.impl.armortrims.ArmorTrimPackage;
import dev.lrxh.neptune.feature.cosmetics.impl.killmessage.KillMessageCosmetic;
import dev.lrxh.neptune.feature.cosmetics.impl.killmessage.KillMessagePackage;
import dev.lrxh.neptune.providers.manager.IService;
import dev.lrxh.neptune.utils.ConfigFile;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class CosmeticService extends IService implements ICosmeticService {
    private static CosmeticService instance;
    public Map<Cosmetic, Map<String, ? extends CosmeticPackage>> cosmetics
            = new HashMap<>();

    public static CosmeticService get() {
        if (instance == null) instance = new CosmeticService();

        return instance;
    }

    @Override
    public void load() {
        KillMessageCosmetic.get().load();
        ArmorTrimCosmetic.get().load();
    }

    @Override
    public void save() {

    }

    public void registerCosmetic(ICosmetic cosmetic) {
        cosmetics.put((Cosmetic) cosmetic, (Map<String, CosmeticPackage>) cosmetic.getPackages());
    }

    public Map<String, ? extends CosmeticPackage> getPackages(ICosmetic cosmetic) {
        return cosmetics.getOrDefault((Cosmetic) cosmetic, null);
    }
    public Map<String, KillMessagePackage> getKillMessagePackages() {
        return (Map<String, KillMessagePackage>) getPackages(KillMessageCosmetic.get());
    }
    public Map<String, ArmorTrimPackage> getArmorTrimPackages() {
        return (Map<String, ArmorTrimPackage>) getPackages(ArmorTrimCosmetic.get());
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getCosmeticsConfig();
    }
}
