package dev.lrxh.neptune.providers.placeholder;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.data.GlobalStats;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.experimental.UtilityClass;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@UtilityClass
public class PlaceholderUtil {

    public List<String> format(List<String> lines, Player player) {
        List<String> formattedLines = new ArrayList<>();

        for (String line : lines) {
            formattedLines.add(format(line, player));
        }

        return formattedLines;
    }

    public Component format(Component component, Player player) {
        return component.replaceText(builder -> builder
                .match(Pattern.compile("<.*?>"))
                .replacement((match, builder1) -> {
                    String placeholder = match.group();
                    String replacement = format(placeholder, player);
                    return Component.text(replacement);
                }));
    }

    public String format(String line, Player player) {
        Profile profile = API.getProfile(player);
        if (profile == null)
            return line;
        ProfileState state = profile.getState();

        line = line.replaceAll("<online>", String.valueOf(Bukkit.getServer().getOnlinePlayers().size()));
        line = line.replaceAll("<queued>", String.valueOf(QueueService.get().getQueueSize()));
        line = line.replaceAll("<in-match>", String.valueOf(MatchService.get().matches.size()));
        line = line.replaceAll("<player>", player.getName());
        line = line.replaceAll("<ping>", String.valueOf((PlayerUtil.getPing(player))));
        line = line.replaceAll("<division>", profile.getGameData().getGlobalStats().getDivision().getDisplayName());

        GlobalStats globalStats = profile.getGameData().getGlobalStats();
        line = line.replaceAll("<wins>", String.valueOf(globalStats.getWins()));
        line = line.replaceAll("<losses>", String.valueOf(globalStats.getLosses()));
        line = line.replaceAll("<currentStreak>", String.valueOf(globalStats.getCurrentStreak()));

        if (state.equals(ProfileState.IN_QUEUE)) {
            QueueEntry queueEntry = QueueService.get().get(player.getUniqueId());
            if (queueEntry == null)
                return line;
            line = line.replaceAll("<kit>", queueEntry.getKit().getDisplayName());
            line = line.replaceAll("<maxPing>", String.valueOf(profile.getSettingData().getMaxPing()));
            line = line.replaceAll("<time>", String.valueOf(queueEntry.getTime().formatTime()));
            line = line.replaceAll("<kit_division>", profile.getGameData().get(queueEntry.getKit()).getDivision().getDisplayName());
        }

        if (state.equals(ProfileState.IN_KIT_EDITOR)) {
            line = line.replaceAll("<kit>", profile.getGameData().getKitEditor().getDisplayName());
        }

        Party party = profile.getGameData().getParty();
        if (party != null) {
            line = line.replaceAll("<leader>", party.getLeaderName());
            line = line.replaceAll("<size>", String.valueOf(party.getUsers().size()));
            line = line.replaceAll("<party-max>", String.valueOf(party.getMaxUsers()));
        }

        if (profile.getMatch() != null) {
            Match match = profile.getMatch();
            Participant participant = match.getParticipant(player.getUniqueId());

            if (profile.getState() == ProfileState.IN_SPECTATOR) {
                if (match instanceof SoloFightMatch soloFightMatch) {
                    Participant red = soloFightMatch.getParticipantA();
                    Participant blue = soloFightMatch.getParticipantB();

                    line = line.replaceAll("<playerRed_name>", red != null ? red.getNameUnColored() : "N/A");
                    line = line.replaceAll("<playerBlue_name>", blue != null ? blue.getNameUnColored() : "N/A");
                    line = line.replaceAll("<playerRed_ping>",
                            String.valueOf(PlayerUtil.getPing(red != null ? red.getPlayer() : null)));
                    line = line.replaceAll("<playerBlue_ping>",
                            String.valueOf(PlayerUtil.getPing(blue != null ? blue.getPlayer() : null)));

                    if (match.getKit().is(KitRule.BED_WARS)) {
                        line = line.replaceAll("<red-bed-status>", red != null && !red.isBedBroken() ? "&a✔" : "&c✘");
                        line = line.replaceAll("<blue-bed-status>",
                                blue != null && !blue.isBedBroken() ? "&a✔" : "&c✘");
                    }
                } else if (match instanceof TeamFightMatch teamFightMatch) {
                    MatchTeam red = teamFightMatch.getTeamA();
                    MatchTeam blue = teamFightMatch.getTeamB();

                    line = line.replaceAll("<alive-red>", String.valueOf(red.getAliveParticipants()));
                    line = line.replaceAll("<max-red>", String.valueOf(red.getParticipants().size()));
                    line = line.replaceAll("<alive-blue>", String.valueOf(blue.getAliveParticipants()));
                    line = line.replaceAll("<max-blue>", String.valueOf(blue.getParticipants().size()));

                    if (match.getKit().is(KitRule.BED_WARS)) {
                        line = line.replaceAll("<red-bed-status>", !red.isBedBroken() ? "&a✔" : "&c✘");
                        line = line.replaceAll("<blue-bed-status>", !blue.isBedBroken() ? "&a✔" : "&c✘");
                    }
                } else if (match instanceof FfaFightMatch ffaFightMatch) {
                    line = line.replaceAll("<alive>", String
                            .valueOf(ffaFightMatch.getParticipants().size() - ffaFightMatch.deadParticipants.size()));
                    line = line.replaceAll("<max>", String.valueOf(ffaFightMatch.getParticipants().size()));
                }
                line = line.replaceAll("<kit_division>", profile.getGameData().get(profile.getMatch().getKit()).getDivision().getDisplayName());
            }

            if (participant != null) {
                line = line.replaceAll("<hits>", String.valueOf(participant.getHits()));
                line = line.replaceAll("<combo>",
                        participant.getCombo() > 1 ? "&e(" + participant.getCombo() + " Combo)" : "");

                if (match instanceof SoloFightMatch soloFightMatch) {

                    Participant redPlayer = soloFightMatch.getParticipantA();
                    Participant bluePlayer = soloFightMatch.getParticipantB();

                    if (profile.getState().equals(ProfileState.IN_GAME)) {
                        Participant opponent = participant.getOpponent();
                        Player opponentPlayer = participant.getOpponent().getPlayer();
                        line = line.replaceAll("<opponent>", participant.getOpponent().getNameUnColored());
                        line = line.replaceAll("<opponent-ping>",
                                String.valueOf(opponentPlayer == null ? 0 : opponentPlayer.getPing()));

                        line = line.replaceAll("<opponent-combo>",
                                opponent.getCombo() > 1 ? "&e(" + opponent.getCombo() + " Combo)" : "");
                        line = line.replaceAll("<opponent-hits>", String.valueOf(opponent.getHits()));
                        line = line.replaceAll("<diffrence>", participant.getHitsDifference(opponent));

                        if (match.getRounds() > 1) {
                            line = line.replaceAll("<maxPoints>", String.valueOf(soloFightMatch.getRounds()));
                            line = line.replaceAll("<points>", String.valueOf(participant.getPoints()));
                            line = line.replaceAll("<opponent-points>", String.valueOf(opponent.getPoints()));
                        }

                        if (match.getKit().is(KitRule.BED_WARS)) {
                            line = line.replaceAll("<bed-status>", !participant.isBedBroken() ? "&a✔" : "&c1");
                            line = line.replaceAll("<opponent-bed-status>", !opponent.isBedBroken() ? "&a✔" : "&c1");
                        }
                    }

                    if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
                        line = line.replaceAll("<playerRed_name>", redPlayer.getNameUnColored());
                        line = line.replaceAll("<playerBlue_name>", bluePlayer.getNameUnColored());

                        line = line.replaceAll("<playerRed_ping>",
                                String.valueOf(PlayerUtil.getPing(redPlayer.getPlayer())));
                        line = line.replaceAll("<playerBlue_ping>",
                                String.valueOf(PlayerUtil.getPing(bluePlayer.getPlayer())));

                        if (match.getKit().is(KitRule.BED_WARS)) {
                            line = line.replaceAll("<red-bed-status>", !redPlayer.isBedBroken() ? "&a✔" : "&c1");
                            line = line.replaceAll("<blue-bed-status>", !bluePlayer.isBedBroken() ? "&a✔" : "&c1");

                        }
                    }
                }
                if (match instanceof TeamFightMatch teamFightMatch) {
                    MatchTeam matchTeam = teamFightMatch
                            .getParticipantTeam(teamFightMatch.getParticipant(player.getUniqueId()));
                    MatchTeam opponentTeam = matchTeam.equals(teamFightMatch.getTeamA()) ? teamFightMatch.getTeamB()
                            : teamFightMatch.getTeamA();

                    if (profile.getState().equals(ProfileState.IN_GAME)) {
                        line = line.replaceAll("<alive>", String.valueOf(matchTeam.getAliveParticipants()));
                        line = line.replaceAll("<max>", String.valueOf(matchTeam.getParticipants().size()));
                        line = line.replaceAll("<alive-opponent>", String.valueOf(opponentTeam.getAliveParticipants()));
                        line = line.replaceAll("<max-opponent>", String.valueOf(opponentTeam.getParticipants().size()));

                        if (match.getRounds() > 1) {
                            line = line.replaceAll("<maxPoints>", String.valueOf(match.getRounds()));
                            line = line.replaceAll("<points>", String.valueOf(matchTeam.getPoints()));
                            line = line.replaceAll("<opponent-points>", String.valueOf(opponentTeam.getPoints()));
                        }

                        if (match.getKit().is(KitRule.BED_WARS)) {
                            line = line.replaceAll("<team-bed-status>",
                                    !matchTeam.isBedBroken() ? "&a✔" : "&c" + matchTeam.getAliveParticipants());
                            line = line.replaceAll("<opponent-team-bed-status>",
                                    !opponentTeam.isBedBroken() ? "&a✔" : "&c" + opponentTeam.getAliveParticipants());
                        }
                    }

                    if (profile.getState().equals(ProfileState.IN_SPECTATOR)) {
                        MatchTeam redTeam = teamFightMatch.getTeamA();
                        MatchTeam blueTeam = teamFightMatch.getTeamB();

                        line = line.replaceAll("<alive-red>", String.valueOf(redTeam.getAliveParticipants()));
                        line = line.replaceAll("<max-red>", String.valueOf(redTeam.getParticipants().size()));
                        line = line.replaceAll("<alive-blue>", String.valueOf(blueTeam.getAliveParticipants()));
                        line = line.replaceAll("<max-blue>", String.valueOf(blueTeam.getParticipants().size()));

                        if (match.getKit().is(KitRule.BED_WARS)) {
                            line = line.replaceAll("<red-bed-status>",
                                    !redTeam.isBedBroken() ? "&a✔" : "&c" + matchTeam.getAliveParticipants());
                            line = line.replaceAll("<red-bed-status>",
                                    !blueTeam.isBedBroken() ? "&a✔" : "&c" + matchTeam.getAliveParticipants());
                        }
                    }
                }

                if (match instanceof FfaFightMatch ffaFightMatch) {
                    line = line.replaceAll("<alive>",
                            String.valueOf(ffaFightMatch.getParticipants().size() - ffaFightMatch.deadParticipants.size()));
                    line = line.replaceAll("<max>", String.valueOf(ffaFightMatch.getParticipants().size()));
                }
            }

            line = line.replaceAll("<kit>", match.getKit().getDisplayName());
            line = line.replaceAll("<arena>", match.getArena().getDisplayName());
            line = line.replaceAll("<time>", match.getTime().formatTime());
        }
        if (Neptune.get().isPlaceholder() && PlaceholderAPIPlugin.getInstance().isEnabled()) {
            return PlaceholderAPI.setPlaceholders(player, line);
        }

        return line;
    }
}
