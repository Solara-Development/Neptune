package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.feature.leaderboard.LeaderboardService;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class LeaderboardPlaceholder implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return LeaderboardService.get().PATTERN.matcher(string).matches();
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        return LeaderboardService.get().getPlaceholder(string);
    }
}
