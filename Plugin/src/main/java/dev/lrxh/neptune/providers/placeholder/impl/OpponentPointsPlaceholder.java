package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.Placeholder;
import org.bukkit.OfflinePlayer;

public class OpponentPointsPlaceholder implements Placeholder {
    @Override
    public boolean match(String string) {
        return string.equals("opponent-points");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return string;
        Match match = profile.getMatch();
        Participant playerParticipant = match.getParticipant(player.getUniqueId());
        if (profile.getState() != ProfileState.IN_GAME) return "";
        if (match instanceof SoloFightMatch) {
            return String.valueOf(playerParticipant.getOpponent().getPoints());
        } else if (match instanceof TeamFightMatch teamFightMatch) {
            MatchTeam opponentTeam = teamFightMatch.getParticipantTeam(playerParticipant).equals(teamFightMatch.getTeamA()) ? teamFightMatch.getTeamB() : teamFightMatch.getTeamA();
            return String.valueOf(opponentTeam.getPoints());
        }
        return "";
    }
}
