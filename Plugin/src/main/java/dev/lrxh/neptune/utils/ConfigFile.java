package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.Neptune;
import lombok.Getter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Getter
public class ConfigFile {
    private final File file;
    private final YamlConfiguration configuration;

    public ConfigFile(String name) {
        File dataFolder = new File(Neptune.get().getDataFolder().getParentFile(), "Neptune");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        this.file = new File(dataFolder, name + ".yml");

        if (!file.exists()) {
            try (InputStream in = Neptune.get().getResource(name + ".yml")) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                } else {
                    if (!file.createNewFile()) {
                        throw new IOException("File was not created.");
                    }
                }
            } catch (IOException e) {
                ServerUtils.error("Error occurred creating config file: " + e);
            }
        }

        this.configuration = new YamlConfiguration();
        try {
            this.configuration.load(this.file);
        } catch (IOException | InvalidConfigurationException e) {
            ServerUtils.error("Failed to load '" + name + ".yml'" + e);
            Neptune.get().setErrored();
        }
    }

    public void save() {
        try {
            configuration.save(file);
        } catch (IOException e) {
            ServerUtils.error("Error occurred saving config: " + e);
        }
    }
}
