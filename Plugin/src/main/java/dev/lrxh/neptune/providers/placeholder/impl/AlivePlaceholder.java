package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class AlivePlaceholder implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.equals("alive");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return string;
        Match match = profile.getMatch();
        if (match == null) return "";
        if (match instanceof TeamFightMatch tfm) {
            return String.valueOf(tfm.getParticipantTeam(match.getParticipant(player.getUniqueId())).getAliveParticipants());
        } else if (match instanceof FfaFightMatch ffm) {
            return String.valueOf(ffm.getParticipants().size() - ffm.deadParticipants.size());
        } else return "";
    }
}
