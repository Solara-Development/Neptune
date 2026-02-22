package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KitPlaceholders implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.startsWith("kit_");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());

        Kit kit = null;

        Pattern pattern = Pattern.compile("kit_(?:(?<kit>[A-Za-z0-9_,.-]+)_)?(?<type>[A-Za-z_]+)");
        Matcher matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid string for kit placeholder: " + string);
        }
        System.out.println(string);
        String resultKit = null;
        try { resultKit = matcher.group("kit"); } catch (Exception ignored) {}
        String type = matcher.group("type");
        if (resultKit != null) kit = KitService.get().getKitByName(resultKit);
        if (profile == null) return null;

        Match match = profile.getMatch();
        QueueEntry queue = QueueService.get().get(profile.getPlayerUUID());
        if (kit != null && type.equals("name")) return kit.getDisplayName();
        else if (profile.hasState(ProfileState.IN_QUEUE) && queue != null)
            kit = queue.getKit();
        else if (profile.hasState(ProfileState.IN_KIT_EDITOR))
            kit = profile.getGameData().getKitEditor();
        else if (profile.hasState(ProfileState.IN_GAME, ProfileState.IN_SPECTATOR) && match != null)
            kit = match.getKit();

        if (kit == null) return null;
        if (type == null) return kit.getDisplayName();

        KitData kitData = profile.getGameData().get(kit);
        switch (type) {
            case "name" -> { return kit.getDisplayName(); }
            case "division" -> { return kitData.getDivision().getDisplayName(); }
            case "rounds" -> { return kit.is(KitRule.BEST_OF_THREE) ? "3" : "1"; }
            case "current_win_streak" -> { return String.valueOf(kitData.getCurrentStreak()); }
            case "best_win_streak" -> { return String.valueOf(kitData.getBestStreak()); }
            case "wins" -> { return String.valueOf(kitData.getWins()); }
            case "losses" -> { return String.valueOf(kitData.getLosses()); }
            case "kills" -> { return String.valueOf(kitData.getKills()); }
            case "deaths" -> { return String.valueOf(kitData.getDeaths()); }
            case "elo" -> { return String.valueOf(kitData.getElo()); }
            case "queued" -> { return String.valueOf(kit.getQueue()); }
            case "in_match" -> { return String.valueOf(kit.getPlaying()); }
        }
        return null;
    }
}
