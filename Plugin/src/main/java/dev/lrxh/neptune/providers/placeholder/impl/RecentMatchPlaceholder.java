package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.profile.data.MatchHistory;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecentMatchPlaceholder implements Placeholder {
    private static final Pattern PATTERN = Pattern.compile("recent_match_(\\d+)_(opponent|kit|arena|date|time|unix_timestamp)");

    @Override
    public boolean match(String string) {
        boolean matches = PATTERN.matcher(string).matches();
        return matches;
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return string;

        Matcher matcher = PATTERN.matcher(string);
        if (!matcher.matches()) return string;

        int matchHistoryIndex = Integer.parseInt(matcher.group(1));
        String type = matcher.group(2);
        if (profile.getGameData().getMatchHistories().size() < matchHistoryIndex) return "";
        List<MatchHistory> matchHistories = profile.getGameData().getMatchHistories();
        Collections.reverse(matchHistories);
        MatchHistory history = matchHistories.get(matchHistoryIndex - 1);
        return switch (type) {
            case "opponent" -> history.getOpponentName();
            case "kit" -> history.getKitName();
            case "arena" -> history.getArenaName();
            case "date" -> history.getDate();
            case "time" -> history.getTime();
            case "unix_timestamp" ->
                    String.valueOf(LocalDateTime.parse(history.getTime()).toEpochSecond(ZoneOffset.UTC));
            default -> string;
        };
    }
}
