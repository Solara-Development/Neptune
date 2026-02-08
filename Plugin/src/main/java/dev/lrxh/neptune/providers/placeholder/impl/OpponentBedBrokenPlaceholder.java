package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class OpponentBedBrokenPlaceholder implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.equals("opponent-bed-broken");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return string;
        Match match = profile.getMatch();
        if (profile.getState() != ProfileState.IN_GAME || match == null || !match.getKit().is(KitRule.BED_WARS))
            return "";
        Participant playerParticipant = match.getParticipant(player.getUniqueId());
        if (match instanceof SoloFightMatch) {
            return playerParticipant.getOpponent().isBedBroken() ? "true" : "false";
        } else if (match instanceof TeamFightMatch teamFightMatch) {
            MatchTeam opponentTeam = teamFightMatch.getParticipantTeam(playerParticipant).equals(teamFightMatch.getTeamA()) ? teamFightMatch.getTeamB() : teamFightMatch.getTeamA();
            return opponentTeam.isBedBroken() ? "true" : "false";
        }
        return "";
    }
}
