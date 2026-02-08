package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class InMatchPlaceholder implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.equals("in-match");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        return String.valueOf(MatchService.get().matches.size());
    }
}
