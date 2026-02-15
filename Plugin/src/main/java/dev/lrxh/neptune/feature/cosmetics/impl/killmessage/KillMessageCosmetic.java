package dev.lrxh.neptune.feature.cosmetics.impl.killmessage;

import dev.lrxh.api.features.cosmetics.killmessages.IKillMessageCosmetic;
import dev.lrxh.api.features.cosmetics.killmessages.IKillMessagePackage;
import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.feature.cosmetics.impl.Cosmetic;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class KillMessageCosmetic extends Cosmetic implements IKillMessageCosmetic {
    private static KillMessageCosmetic instance;
    public static KillMessageCosmetic get() {
        if (instance == null) instance = new KillMessageCosmetic();
        return instance;
    }

    public String key() {
        return "killmessages";
    }

    public Map<String, KillMessagePackage> packages = new HashMap<>();
    public void load() {
        packages.clear();
        FileConfiguration killMessagesConfig = ConfigService.get().getKillMessagesConfig().getConfiguration();
        if (killMessagesConfig.contains("KILL_MESSAGES")) {
            for (String packageName : getKeys(killMessagesConfig, "KILL_MESSAGES")) {
                String path = "KILL_MESSAGES." + packageName + ".";
                String displayName = killMessagesConfig.getString(path + "DISPLAY_NAME");
                Material material = Material.getMaterial(Objects.requireNonNull(killMessagesConfig.getString(path + "MATERIAL")));
                List<String> description = killMessagesConfig.getStringList(path + "DESCRIPTION");
                int slot = killMessagesConfig.getInt(path + "SLOT");
                List<String> messages = killMessagesConfig.getStringList(path + "MESSAGES");

                packages.put(packageName, new KillMessagePackage(packageName, displayName, material, description, slot, messages));
            }
        }
    }

    public KillMessagePackage getDefault() {
        return packages.get("DEFAULT");
    }
    public KillMessagePackage getOrDefault(String packageName) {
        if (packages.containsKey(packageName)) return packages.get(packageName);
        return getDefault();
    }

    public void addPackage(IKillMessagePackage killMessagePackage) {
        packages.put(killMessagePackage.getName(), (KillMessagePackage) killMessagePackage);
    }
    public YamlConfiguration getConfig() {
        return ConfigService.get().getKillMessagesConfig().getConfiguration();
    }
}
