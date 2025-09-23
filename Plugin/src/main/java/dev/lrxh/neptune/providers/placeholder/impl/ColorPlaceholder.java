package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class ColorPlaceholder implements Placeholder {
    @Override
    public boolean match(String string) {
        return string.equals("color");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player);
        if (profile == null)
            return "";
        Match match = profile.getMatch();
        if (match == null)
            return "";
        Participant participant = match.getParticipant(player.getUniqueId());
        if (participant == null)
            return "";
        return participant.getColor().getColor();
    }
}
