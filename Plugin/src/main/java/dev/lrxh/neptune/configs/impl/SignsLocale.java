package dev.lrxh.neptune.configs.impl;

import dev.lrxh.neptune.configs.ConfigService;
import dev.lrxh.neptune.configs.impl.handler.DataType;
import dev.lrxh.neptune.configs.impl.handler.IDataAccessor;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public enum SignsLocale implements IDataAccessor {
    CUSTOM_KIT_NAME("CUSTOM_KIT.NAME", "Create custom kit sign lines. <input> = typed value.", DataType.STRING_LIST, "<input>", "Enter kit name"),
    CUSTOM_KIT_AMOUNT("CUSTOM_KIT.AMOUNT", "Custom kit item amount sign lines. <input> = typed value.", DataType.STRING_LIST, "<input>", "Enter amount", "1- 64"),
    CUSTOM_KIT_HEALTH("CUSTOM_KIT.HEALTH", "Custom kit health sign lines. <input> = typed value.", DataType.STRING_LIST, "<input>", "Enter max health", "1- 40"),
    ITEM_BROWSER_SEARCH("ITEM_BROWSER.SEARCH", "Item browser search sign lines. <input> = typed value.", DataType.STRING_LIST, "<input>", "Enter search");

    private final String path;
    private final String comment;
    private final List<String> defaultValue = new ArrayList<>();
    private final DataType dataType;

    SignsLocale(String path, @Nullable String comment, DataType dataType, String... defaultValue) {
        this.path = path;
        this.comment = comment;
        this.defaultValue.addAll(Arrays.asList(defaultValue));
        this.dataType = dataType;
    }

    SignsLocale(String path, DataType dataType, String... defaultValue) {
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
        return ConfigService.get().getSignsConfig();
    }

    public void update() {}
}
