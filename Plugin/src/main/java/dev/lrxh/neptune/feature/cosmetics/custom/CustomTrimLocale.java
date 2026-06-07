package dev.lrxh.neptune.feature.cosmetics.custom;

import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.handler.DataType;
import dev.lrxh.neptune.configs.impl.handler.IDataAccessor;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Config entries for the custom per-piece armor trim system.
 * Stored in cosmetics.yml under CUSTOM_TRIM section.
 */
@Getter
public enum CustomTrimLocale implements IDataAccessor {

    ENABLED("CUSTOM_TRIM.ENABLED", "Whether the custom per-piece trim system is enabled", DataType.BOOLEAN, "true"),

    // List of trim materials available in the picker GUI
    MATERIALS("CUSTOM_TRIM.AVAILABLE_MATERIALS",
            "Trim material registry keys. See: https://minecraft.wiki/w/Smithing_Template#Trim_materials",
            DataType.STRING_LIST,
            "amethyst", "copper", "diamond", "emerald", "gold",
            "iron", "lapis", "netherite", "quartz", "redstone"),

    // List of trim patterns available in the picker GUI
    PATTERNS("CUSTOM_TRIM.AVAILABLE_PATTERNS",
            "Trim pattern registry keys. See: https://minecraft.wiki/w/Smithing_Template#Trim_patterns",
            DataType.STRING_LIST,
            "bolt", "coast", "dune", "eye", "flow", "host",
            "raiser", "rib", "sentry", "shaper", "silence",
            "snout", "spire", "tide", "vex", "wayfinder",
            "ward", "wild"),

    // ---- GUI text ----
    MENU_TITLE("CUSTOM_TRIM.MENU.TITLE", DataType.STRING, "&b&lCustom Armor Trims"),
    PIECE_MENU_TITLE("CUSTOM_TRIM.MENU.PIECE_TITLE", DataType.STRING, "&b&lEdit <piece>"),
    MATERIAL_ROW_LABEL("CUSTOM_TRIM.MENU.MATERIAL_LABEL", DataType.STRING, "&7Material: &b<material>"),
    PATTERN_ROW_LABEL("CUSTOM_TRIM.MENU.PATTERN_LABEL", DataType.STRING, "&7Pattern: &b<pattern>"),

    NO_TRIM_NAME("CUSTOM_TRIM.MENU.NO_TRIM_NAME", DataType.STRING, "&cNo Trim"),
    NO_TRIM_LORE("CUSTOM_TRIM.MENU.NO_TRIM_LORE", DataType.STRING_LIST,
            "&7Remove the trim from this piece"),

    CLEAR_ALL_NAME("CUSTOM_TRIM.MENU.CLEAR_ALL_NAME", DataType.STRING, "&cClear All Trims"),
    CLEAR_ALL_LORE("CUSTOM_TRIM.MENU.CLEAR_ALL_LORE", DataType.STRING_LIST,
            "&7Remove all custom trims and revert to package"),

    ACTIVATE_NAME("CUSTOM_TRIM.MENU.ACTIVATE_NAME", DataType.STRING, "&aActivate Custom Trims"),
    DEACTIVATE_NAME("CUSTOM_TRIM.MENU.DEACTIVATE_NAME", DataType.STRING, "&cDeactivate Custom Trims"),
    TOGGLE_LORE("CUSTOM_TRIM.MENU.TOGGLE_LORE", DataType.STRING_LIST,
            "&7Toggle whether your custom per-piece",
            "&7trims override the selected package"),

    SELECTED_GLOW("CUSTOM_TRIM.MENU.SELECTED_GLOW", "Whether selected material/pattern glows", DataType.BOOLEAN, "true");

    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    CustomTrimLocale(String path, DataType dataType, String... vals) {
        this.path = path;
        this.comment = null;
        defaultValue.addAll(Arrays.asList(vals));
        this.dataType = dataType;
    }

    CustomTrimLocale(String path, String comment, DataType dataType, String... vals) {
        this.path = path;
        this.comment = comment;
        defaultValue.addAll(Arrays.asList(vals));
        this.dataType = dataType;
    }

    @Override
    public String getHeader() { return "Custom Per-Piece Armor Trim Configuration"; }

    @Override
    public ConfigFile getConfigFile() {
        return ConfigService.get().getCosmeticsConfig();
    }

    @Override
    public boolean resetUnknown() {
        return false;
    }

    @Override
    public void update() {}
}
