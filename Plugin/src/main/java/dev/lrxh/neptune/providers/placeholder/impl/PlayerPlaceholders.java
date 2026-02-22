package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.profile.data.GlobalStats;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class PlayerPlaceholders implements PAPIPlaceholder {

    @Override
    public boolean match(String string) {
        return true;
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return null;
        GlobalStats globalStats = profile.getGameData().getGlobalStats();
        SettingData settings = profile.getSettingData();
        switch (string) {
            case "state" -> { return profile.getProfileState(); }
            case "kdr" -> { return String.valueOf(globalStats.getKdr()); }
            case "elo" -> { return String.valueOf(globalStats.getElo()); }
            case "wins" -> { return String.valueOf(globalStats.getWins()); }
            case "kills" -> { return String.valueOf(globalStats.getKills()); }
            case "deaths" -> { return String.valueOf(globalStats.getDeaths()); }
            case "losses" -> { return String.valueOf(globalStats.getLosses()); }
            case "division" -> { return globalStats.getDivision().getDisplayName(); }
            case "win_loss_ratio" -> { return String.valueOf(globalStats.getWinRatio()); }
            case "best_win_streak" -> { return String.valueOf(globalStats.getBestStreak()); }
            case "current_win_streak" -> { return String.valueOf(globalStats.getCurrentStreak()); }
            case "played" -> { return String.valueOf(globalStats.getWins() + globalStats.getLosses()); }
            case "max_ping" -> { return String.valueOf(settings.getMaxPing()); }
            case "kill_effect" -> { return settings.getKillEffect().getDisplayName(); }
            case "kill_message" -> { return settings.getKillMessagePackage().getDisplayName(); }
            case "armor_trim" -> { return settings.getArmorTrimPackage().getDisplayName(); }
            case "shield_pattern" -> { return settings.getShieldPatternPackage().getDisplayName(); }
        }
        return null;
    }
}
