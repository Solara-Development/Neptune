package dev.lrxh.neptune.configs;

import dev.lrxh.neptune.configs.impl.*;
import dev.lrxh.neptune.utils.ConfigFile;
import lombok.Getter;


@Getter
public class ConfigService {
    private static ConfigService instance;
    private ConfigFile messagesConfig;
    private ConfigFile arenasConfig;
    private ConfigFile kitsConfig;
    private ConfigFile mainConfig;
    private ConfigFile scoreboardConfig;
    private ConfigFile hotbarConfig;
    private ConfigFile menusConfig;
    private ConfigFile divisionsConfig;
    private ConfigFile cosmeticsConfig;
    private ConfigFile killMessagesConfig;
    private ConfigFile soundsConfig;

    public static ConfigService get() {
        if (instance == null) instance = new ConfigService();

        return instance;
    }

    public void load() {
        messagesConfig = new ConfigFile("messages");
        arenasConfig = new ConfigFile("arenas");
        kitsConfig = new ConfigFile("kits");
        mainConfig = new ConfigFile("settings");
        scoreboardConfig = new ConfigFile("scoreboard");
        hotbarConfig = new ConfigFile("hotbar");
        menusConfig = new ConfigFile("menus");
        divisionsConfig = new ConfigFile("divisions");
        cosmeticsConfig = new ConfigFile("cosmetics");
        killMessagesConfig = new ConfigFile("kill-messages");
        soundsConfig = new ConfigFile("sounds");

        initialize();
    }

    public void initialize() {
        HotbarLocale.LOBBY_PARTY_CREATE_NAME.load();
        MessagesLocale.MATCH_FOUND.load();
        SettingsLocale.SPAWN_LOCATION.load();
        MenusLocale.FILTER_NAME.load();
        ScoreboardLocale.TITLE.load();
        CosmeticsLocale.LIGHTNING_DISPLAY_NAME.load();
        SoundsLocale.MATCH_START.load();

        HotbarLocale.LOBBY_PARTY_CREATE_NAME.update();
        MessagesLocale.MATCH_FOUND.update();
        SettingsLocale.SPAWN_LOCATION.update();
        MenusLocale.FILTER_NAME.update();
        ScoreboardLocale.TITLE.update();
        CosmeticsLocale.LIGHTNING_DISPLAY_NAME.update();
        SoundsLocale.MATCH_START.update();
    }
}
