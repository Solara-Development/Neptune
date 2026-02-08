package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.handler.DataType;
import dev.lrxh.neptune.configs.impl.handler.IDataAccessor;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum CosmeticsLocale implements IDataAccessor {
    NONE_DISPLAY_NAME("KILL-EFFECTS.NONE.DISPLAY-NAME", DataType.STRING, "&bNone"),
    NONE_SLOT("KILL-EFFECTS.NONE.SLOT", DataType.INT, "10"),
    NONE_MATERIAL("KILL-EFFECTS.NONE.MATERIAL", DataType.STRING, "BARRIER"),
    LIGHTNING_DISPLAY_NAME("KILL-EFFECTS.LIGHTNING.DISPLAY-NAME", DataType.STRING, "&bLightning"),
    LIGHTNING_SLOT("KILL-EFFECTS.LIGHTNING.SLOT", DataType.INT, "11"),
    LIGHTNING_MATERIAL("KILL-EFFECTS.LIGHTNING.MATERIAL", DataType.STRING, "NETHER_STAR"),
    FIREWORKS_DISPLAY_NAME("KILL-EFFECTS.FIREWORKS.DISPLAY-NAME", DataType.STRING, "&bFireworks"),
    FIREWORKS_SLOT("KILL-EFFECTS.FIREWORKS.SLOT", DataType.INT, "12"),
    FIREWORKS_MATERIAL("KILL-EFFECTS.FIREWORKS.MATERIAL", DataType.STRING, "FIREWORK_ROCKET"),
    ANGRY_DISPLAY_NAME("KILL-EFFECTS.ANGRY.DISPLAY-NAME", DataType.STRING, "&bAngry"),
    ANGRY_SLOT("KILL-EFFECTS.ANGRY.SLOT", DataType.INT, "13"),
    ANGRY_MATERIAL("KILL-EFFECTS.ANGRY.MATERIAL", DataType.STRING, "REDSTONE"),
    HEARTS_DISPLAY_NAME("KILL-EFFECTS.HEARTS.DISPLAY-NAME", DataType.STRING, "&bHearts"),
    HEARTS_SLOT("KILL-EFFECTS.HEARTS.SLOT", DataType.INT, "14"),
    HEARTS_MATERIAL("KILL-EFFECTS.HEARTS.MATERIAL", DataType.STRING, "SUNFLOWER"),
    LAVA_DISPLAY_NAME("KILL-EFFECTS.LAVA.DISPLAY-NAME", DataType.STRING, "&bLava"),
    LAVA_SLOT("KILL-EFFECTS.LAVA.SLOT", DataType.INT, "15"),
    LAVA_MATERIAL("KILL-EFFECTS.LAVA.MATERIAL", DataType.STRING, "LAVA_BUCKET");
    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    CosmeticsLocale(String path, DataType dataType, String... defaultValue) {
        this.path = path;
        this.comment = null;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    @Override
    public String getHeader() {
        return "";
    }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getCosmeticsConfig();
    }
    public void update() {}
}
