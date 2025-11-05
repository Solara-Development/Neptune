package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class PlayerRedNamePlaceholder implements Placeholder {
    @Override
    public boolean match(String string) {
        return string.equals("player-red-name");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return string;
        Match match = profile.getMatch();
        if (!(match instanceof SoloFightMatch soloFightMatch)) return "";
        return soloFightMatch.getParticipantA().getNameUnColored();
    }
}
