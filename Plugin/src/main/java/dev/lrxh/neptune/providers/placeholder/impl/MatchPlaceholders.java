package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class MatchPlaceholders implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return true;
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        Profile profile = API.getProfile(player.getUniqueId());
        if (profile == null) return null;
        Match match = profile.getMatch();
        if (match == null) return null;
        Kit kit = match.getKit();
        switch (string) {
            case "loser" -> { return match.getLoserName(); }
            case "winner" -> { return match.getWinnerName(); }
            case "arena" -> { return match.getArena().getDisplayName(); }
            case "round" -> { return String.valueOf(match.getCurrentRound()); }
        }
        Participant participant = match.getParticipant(profile.getPlayer());
        if (participant != null) {
            if (string.equals("color")) return participant.getColor().getColor();
            if (string.equals("opponent_color")) return participant.getOpponent().getColor().getColor();
        }
        if (match instanceof SoloFightMatch sfm) {
            Participant red = sfm.getRedParticipant();
            Participant blue = sfm.getBlueParticipant();
            switch (string) {
                case "red_name" -> { return red.getName(); }
                case "blue_name" -> { return blue.getName(); }
                case "red_combo" -> { return red.getComboMessage(); }
                case "blue_combo" -> { return blue.getComboMessage(); }
                case "red_hits" -> { return String.valueOf(red.getHits()); }
                case "blue_hits" -> { return String.valueOf(blue.getHits()); }
                case "red_points" -> { return String.valueOf(red.getPoints()); }
                case "blue_points" -> { return String.valueOf(blue.getPoints()); }
                case "red_ping" -> { return String.valueOf(red.getPlayer().getPing()); }
                case "blue_ping" -> { return String.valueOf(blue.getPlayer().getPing()); }
                case "red_longest_combo" -> { return String.valueOf(red.getLongestCombo()); }
                case "blue_longest_combo" -> { return String.valueOf(blue.getLongestCombo()); }
                case "red_hit_difference" -> { return String.valueOf(red.getHitsDifference(red)); }
                case "blue_hit_difference" -> { return String.valueOf(blue.getHitsDifference(blue)); }
                case "red_elo" -> { return String.valueOf(red.getProfile().getGameData().getGlobalStats().getElo()); }
                case "blue_elo" -> { return String.valueOf(blue.getProfile().getGameData().getGlobalStats().getElo()); }
            }
            if (participant != null) {
                Participant opponent = participant.getOpponent();
                switch (string) {
                    case "combo" -> { return participant.getComboMessage(); }
                    case "opponent_combo" -> { return opponent.getComboMessage(); }
                    case "hits" -> { return String.valueOf(participant.getHits()); }
                    case "opponent_hits" -> { return String.valueOf(opponent.getHits()); }
                    case "longest_combo" -> { return String.valueOf(participant.getLongestCombo()); }
                    case "opponent_ping" -> { return String.valueOf(opponent.getPlayer().getPing()); }
                    case "opponent_hit_difference" -> { return opponent.getHitsDifference(participant); }
                    case "opponent_longest_combo" -> { return String.valueOf(opponent.getLongestCombo()); }
                    case "opponent_elo" -> { return String.valueOf(opponent.getProfile().getGameData().getGlobalStats().getElo()); }
                }
            }
            if (kit.is(KitRule.BED_WARS)) {
                switch (string) {
                    case "red_bed_broken" -> { return red.getBedMessage(); }
                    case "blue_bed_broken" -> { return blue.getBedMessage(); }
                }
                if (participant != null) {
                    Participant opponent = participant.getOpponent();
                    switch (string) {
                        case "bed_broken" -> { return participant.getBedMessage(); }
                        case "opponent_bed_broken" -> { return opponent.getBedMessage(); }
                    }
                }
            }
        }
        if (match instanceof TeamFightMatch tfm) {
            MatchTeam red = tfm.getRedTeam();
            MatchTeam blue = tfm.getBlueTeam();
            switch (string) {
                case "red_players" -> { return red.getTeamNames(); }
                case "blue_players" -> { return blue.getTeamNames(); }
                case "red_points" -> { return String.valueOf(red.getPoints()); }
                case "blue_points" -> { return String.valueOf(blue.getPoints()); }
                case "red_alive" -> { return String.valueOf(red.getAliveParticipants()); }
                case "red_total" -> { return String.valueOf(red.getParticipants().size()); }
                case "blue_alive" -> { return String.valueOf(blue.getAliveParticipants()); }
                case "blue_total" -> { return String.valueOf(blue.getParticipants().size()); }
                case "red_dead" -> { return String.valueOf(red.getDeadParticipants().size()); }
                case "blue_dead" -> { return String.valueOf(blue.getDeadParticipants().size()); }
            }
            if (participant != null) {
                MatchTeam team = tfm.getParticipantTeam(participant);
                MatchTeam opponent = team.getOpponentTeam();
                switch (string) {
                    case "team_players" -> { return team.getTeamNames(); }
                    case "opponent_players" -> { return opponent.getTeamNames(); }
                    case "team_points" -> { return String.valueOf(team.getPoints()); }
                    case "opponent_points" -> { return String.valueOf(opponent.getPoints()); }
                    case "team_alive" -> { return String.valueOf(team.getAliveParticipants()); }
                    case "team_total" -> { return String.valueOf(team.getParticipants().size()); }
                    case "team_dead" -> { return String.valueOf(team.getDeadParticipants().size()); }
                    case "opponent_alive" -> { return String.valueOf(opponent.getAliveParticipants()); }
                    case "opponent_total" -> { return String.valueOf(opponent.getParticipants().size()); }
                    case "opponent_dead" -> { return String.valueOf(opponent.getDeadParticipants().size()); }
                }
            }
            if (kit.is(KitRule.BED_WARS)) {
                switch (string) {
                    case "red_bed_broken" -> { return red.getBedMessage(); }
                    case "blue_bed_broken" -> { return blue.getBedMessage(); }
                }
                if (participant != null) {
                    MatchTeam team = tfm.getParticipantTeam(participant);
                    MatchTeam opponent = team.getOpponentTeam();
                    switch (string) {
                        case "bed_broken" -> { return team.getBedMessage(); }
                        case "opponent_bed_broken" -> { return opponent.getBedMessage(); }
                    }
                }
            }
        }
        if (match instanceof FfaFightMatch ffm) {
            switch (string) {
                case "max" -> { return String.valueOf(ffm.getParticipants().size()); }
                case "dead" -> { return String.valueOf(ffm.getDeadParticipants().size()); }
                case "is_dead" -> { return ffm.getDeadParticipants().contains(participant) ? "true" : "false"; }
                case "alive" -> { return String.valueOf(ffm.getParticipants().size() - ffm.getDeadParticipants().size()); }
            }
        }
        return null;
    }
}
