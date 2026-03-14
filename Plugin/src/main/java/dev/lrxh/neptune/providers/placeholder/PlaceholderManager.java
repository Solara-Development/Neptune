package dev.lrxh.neptune.providers.placeholder;

import dev.lrxh.neptune.providers.placeholder.impl.*;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlaceholderManager {
    private static PlaceholderManager instance;
    private final List<PAPIPlaceholder> placeholders;

    public PlaceholderManager() {
        this.placeholders = new ArrayList<>();

        placeholders.addAll(Arrays.asList(
                new GlobalPlaceholders(),
                new KitPlaceholders(),
                new LastKitPlaceholder(),
                new LeaderboardPlaceholder(),
                new MatchPlaceholders(),
                new PartyPlaceholders(),
                new PlayerPlaceholders(),
                new RecentMatchPlaceholder(),
                new TimePlaceholder()
        ));
    }

    public static PlaceholderManager get() {
        if (instance == null) instance = new PlaceholderManager();

        return instance;
    }

    public String parse(OfflinePlayer player, String text) {
        for (PAPIPlaceholder placeholder : placeholders) {
            if (placeholder.match(text)) {
                String parsed = placeholder.parse(player, text);
                if (parsed == null) continue;
                text = parsed;
            }
        }
        return text;
    }
}
