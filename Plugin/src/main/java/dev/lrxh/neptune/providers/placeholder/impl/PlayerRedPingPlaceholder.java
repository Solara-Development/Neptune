package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class PlayerRedPingPlaceholder implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.equals("player-red-ping");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return string;
        Match match = profile.getMatch();
        if (!(match instanceof SoloFightMatch soloFightMatch)) return "";
        return String.valueOf(soloFightMatch.getParticipantA().getPlayer().getPing());
    }
}
