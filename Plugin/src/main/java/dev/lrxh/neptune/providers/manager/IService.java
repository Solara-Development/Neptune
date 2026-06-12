package dev.lrxh.neptune.providers.manager;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.utils.ConfigFile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class IService {
    public abstract ConfigFile getConfigFile();

    public abstract void load();

    public abstract void save();

    public <T extends ConfigData> void saveAll(String root, Collection<T> items, Function<T, String> keyFn) {
        if (Neptune.get().isErrored()) return;
        FileConfiguration config = getConfigFile().getConfiguration();
        ConfigurationSection section = config.getConfigurationSection(root);
        Set<String> existingKeys = section != null ? new HashSet<>(section.getKeys(false)) : new HashSet<>();
        for (T item : items) {
            String key = keyFn.apply(item);
            existingKeys.remove(key);
            item.write(config.createSection(root + "." + key));
        }
        for (String stale : existingKeys) {
            config.set(root + "." + stale, null);
        }
        getConfigFile().save();
    }

    public <T> void loadAll(String root, Collection<T> out, BiFunction<String, ConfigurationSection, T> reader) {
        ConfigurationSection section = getConfigFile().getConfiguration().getConfigurationSection(root);
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            try {
                T value = reader.apply(key, section.getConfigurationSection(key));
                if (value != null) out.add(value);
            } catch (Exception e) {
                Neptune.get().getLogger().severe("Error occurred while loading " + root + " with key: " + key);
                Neptune.get().setErrored();
                throw e;
            }
        }
    }

    public Set<String> getKeys(String path) {
        return Objects.requireNonNull(getConfigFile().getConfiguration().getConfigurationSection(path)).getKeys(false);
    }

    public Set<String> getKeys(FileConfiguration config, String path) {
        return Objects.requireNonNull(config.getConfigurationSection(path)).getKeys(false);
    }
}
