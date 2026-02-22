package dev.lrxh.neptune.providers.placeholder;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.party.Party;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.profile.data.GlobalStats;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@UtilityClass
public class PlaceholderUtil {
    public TagResolver getPlaceholders(Player player) {
        TagResolver placeholders = TagResolver.resolver(
            Placeholder.unparsed("online", String.valueOf(Bukkit.getServer().getOnlinePlayers().size())),
            Placeholder.unparsed("queued", String.valueOf(QueueService.get().getQueueSize())),
            Placeholder.unparsed("in-match", String.valueOf(MatchService.get().matches.size()))
        );
        if (player != null) placeholders = TagResolver.resolver(placeholders,
                Placeholder.unparsed("player", player.getName()),
                Placeholder.unparsed("ping", String.valueOf((PlayerUtil.getPing(player))))
        );
        else return placeholders;
        Profile profile = API.getProfile(player);
        if (profile == null) return placeholders;
        GlobalStats globalStats = profile.getGameData().getGlobalStats();
        Party party = profile.getGameData().getParty();
        Match match = profile.getGameData().getMatch();
        QueueEntry queue = QueueService.get().get(profile.getPlayerUUID());
        placeholders = TagResolver.resolver(placeholders, getSettingsResolvers(profile),
            Placeholder.parsed("division", globalStats.getDivision().getDisplayName()),
            Placeholder.unparsed("max-ping", String.valueOf(profile.getSettingData().getMaxPing())),
            Placeholder.unparsed("wins", String.valueOf(globalStats.getWins())),
            Placeholder.unparsed("losses", String.valueOf(globalStats.getLosses())),
            Placeholder.unparsed("win-loss-ratio", String.valueOf(globalStats.getWinRatio())),
            Placeholder.unparsed("kills", String.valueOf(globalStats.getKills())),
            Placeholder.unparsed("deaths", String.valueOf(globalStats.getDeaths())),
            Placeholder.unparsed("elo", String.valueOf(globalStats.getElo())),
            Placeholder.unparsed("played", String.valueOf(globalStats.getWins() + globalStats.getLosses())),
            Placeholder.unparsed("kdr", String.valueOf(globalStats.getKdr())),
            Placeholder.unparsed("current-win-streak", String.valueOf(globalStats.getCurrentStreak())),
            Placeholder.unparsed("best-win-streak", String.valueOf(globalStats.getBestStreak()))
        );
        Kit kit = null;
        if (profile.hasState(ProfileState.IN_QUEUE) && queue != null)
            kit = queue.getKit();
        else if (profile.hasState(ProfileState.IN_KIT_EDITOR))
            kit = profile.getGameData().getKitEditor();
        else if (profile.hasState(ProfileState.IN_GAME, ProfileState.IN_SPECTATOR) && match != null)
            kit = match.getKit();
        if (kit != null) {
            KitData kitData = profile.getGameData().get(kit);
            placeholders = TagResolver.resolver(placeholders,
                Placeholder.parsed("kit", kit.getDisplayName()),
                Placeholder.parsed("kit-division", kitData.getDivision().getName()),
                Placeholder.unparsed("rounds", kit.is(KitRule.BEST_OF_THREE) ? "3" : "1"),
                Placeholder.unparsed("kit-current-win-streak", String.valueOf(kitData.getCurrentStreak())),
                Placeholder.unparsed("kit-best-win-streak", String.valueOf(kitData.getBestStreak())),
                Placeholder.unparsed("kit-wins", String.valueOf(kitData.getWins())),
                Placeholder.unparsed("kit-losses", String.valueOf(kitData.getLosses())),
                Placeholder.unparsed("kit-kills", String.valueOf(kitData.getKills())),
                Placeholder.unparsed("kit-deaths", String.valueOf(kitData.getDeaths())),
                Placeholder.unparsed("kit-elo", String.valueOf(kitData.getElo()))
            );
        }
        if (profile.hasState(ProfileState.IN_QUEUE) && queue != null) {
            placeholders = TagResolver.resolver(placeholders,
                Placeholder.parsed("time", queue.getTime().formatTime())
            );
        }
        if (party != null) {
            placeholders = TagResolver.resolver(placeholders,
                Placeholder.unparsed("party-leader", party.getLeaderName()),
                Placeholder.unparsed("party-size", String.valueOf(party.getUsers().size())),
                Placeholder.unparsed("party-max", String.valueOf(party.getMaxUsers())),
                Placeholder.unparsed("party-privacy", party.isOpen() ? MessagesLocale.PARTY_PRIVACY_OPEN.getString() : MessagesLocale.PARTY_PRIVACY_CLOSED.getString())
            );
        }
        if (match != null) {
            kit = match.getKit();
            Participant participant = match.getParticipant(player);
            placeholders = TagResolver.resolver(placeholders,
                Placeholder.parsed("arena", match.getArena().getDisplayName()),
                Placeholder.unparsed("winner", match.getWinnerName()),
                Placeholder.unparsed("loser", match.getLoserName()),
                Placeholder.unparsed("round", String.valueOf(match.getCurrentRound())),
                Placeholder.unparsed("time", match.getTime().formatTime())
            );
            if (match instanceof SoloFightMatch sfm) {
                Participant red = sfm.getRedParticipant();
                Participant blue = sfm.getBlueParticipant();
                if (participant != null) {
                    Participant opponent = participant.getOpponent();
                    placeholders = TagResolver.resolver(placeholders,
                            Placeholder.parsed("bed-broken", participant.getBedMessage()),
                            Placeholder.parsed("opponent-bed-broken", opponent.getBedMessage()),
                            Placeholder.parsed("opponent-combo", opponent.getComboMessage()),
                            Placeholder.unparsed("combo", participant.getComboMessage()),
                            Placeholder.unparsed("longest-combo", String.valueOf(participant.getLongestCombo())),
                            Placeholder.unparsed("hits", String.valueOf(participant.getHits())),
                            Placeholder.unparsed("hit-difference", String.valueOf(participant.getHitsDifference(opponent))),
                            Placeholder.unparsed("points", String.valueOf(participant.getPoints())),
                            Placeholder.unparsed("opponent", String.valueOf(opponent.getName())),
                            Placeholder.unparsed("opponent-longest-combo", String.valueOf(opponent.getLongestCombo())),
                            Placeholder.unparsed("opponent-hits", String.valueOf(opponent.getHits())),
                            Placeholder.unparsed("opponent-hit-difference", String.valueOf(opponent.getHitsDifference(participant))),
                            Placeholder.unparsed("opponent-points", String.valueOf(opponent.getPoints())),
                            Placeholder.unparsed("opponent-elo", String.valueOf(opponent.getProfile().getGameData().getGlobalStats().getElo())),
                            Placeholder.unparsed("opponent-kit-elo", String.valueOf(opponent.getProfile().getGameData().get(kit).getElo()))
                    );
                    if (opponent.getPlayer() != null) placeholders = TagResolver.resolver(placeholders,
                            Placeholder.unparsed("opponent-ping", String.valueOf(opponent.getPlayer().getPing())));
                }
                placeholders = TagResolver.resolver(placeholders,
                    Placeholder.parsed("red-combo", red.getComboMessage()),
                    Placeholder.parsed("blue-combo", blue.getComboMessage()),
                    Placeholder.parsed("red-elo", String.valueOf(red.getProfile().getGameData().getGlobalStats().getElo())),
                    Placeholder.parsed("blue-elo", String.valueOf(blue.getProfile().getGameData().getGlobalStats().getElo())),
                    Placeholder.parsed("red-kit-elo", String.valueOf(red.getProfile().getGameData().get(kit).getElo())),
                    Placeholder.parsed("blue-elo", String.valueOf(blue.getProfile().getGameData().get(kit).getElo())),
                    Placeholder.parsed("red-bed-broken", red.getBedMessage()),
                    Placeholder.parsed("blue-bed-broken", blue.getBedMessage()),
                    Placeholder.unparsed("red-name", red.getName()),
                    Placeholder.unparsed("red-longest-combo", String.valueOf(red.getLongestCombo())),
                    Placeholder.unparsed("red-hits", String.valueOf(red.getHits())),
                    Placeholder.unparsed("red-hit-difference", red.getHitsDifference(sfm.getBlueParticipant())),
                    Placeholder.unparsed("red-points", String.valueOf(red.getPoints())),
                    Placeholder.unparsed("blue-name", blue.getName()),
                    Placeholder.unparsed("blue-longest-combo", String.valueOf(blue.getLongestCombo())),
                    Placeholder.unparsed("blue-hits", String.valueOf(blue.getHits())),
                    Placeholder.unparsed("blue-hit-difference", blue.getHitsDifference(sfm.getRedParticipant())),
                    Placeholder.unparsed("blue-points", String.valueOf(blue.getPoints()))
                );
                if (kit.is(KitRule.BED_WARS) && participant != null) placeholders = TagResolver.resolver(placeholders,
                        Placeholder.parsed("bed-broken", participant.getBedMessage()),
                        Placeholder.parsed("opponent-bed-broken", participant.getOpponent().getBedMessage())
                );
                if (red.getPlayer() != null) placeholders = TagResolver.resolver(placeholders,
                        Placeholder.unparsed("red-ping", String.valueOf(red.getPlayer().getPing())));
                if (blue.getPlayer() != null) placeholders = TagResolver.resolver(placeholders,
                        Placeholder.unparsed("blue-ping", String.valueOf(blue.getPlayer().getPing())));
            }
            if (match instanceof TeamFightMatch tfm)  {
                MatchTeam red = tfm.getRedTeam();
                MatchTeam blue = tfm.getBlueTeam();
                if (participant != null) {
                    MatchTeam team = tfm.getParticipantTeam(participant);
                    MatchTeam opponent = team.getOpponentTeam();
                    placeholders = TagResolver.resolver(placeholders,
                            Placeholder.parsed("team-bed-broken", team.getBedMessage()),
                            Placeholder.parsed("opponent-bed-broken", opponent.getBedMessage()),
                            Placeholder.unparsed("team-players", team.getTeamNames()),
                            Placeholder.unparsed("team-alive", String.valueOf(team.getAliveParticipants())),
                            Placeholder.unparsed("team-dead", String.valueOf(team.getDeadParticipants().size())),
                            Placeholder.unparsed("team-total", String.valueOf(team.getAliveParticipants() + team.getDeadParticipants().size())),
                            Placeholder.unparsed("team-points", String.valueOf(team.getPoints())),
                            Placeholder.unparsed("opponent-alive", String.valueOf(opponent.getAliveParticipants())),
                            Placeholder.unparsed("opponent-dead", String.valueOf(opponent.getDeadParticipants().size())),
                            Placeholder.unparsed("opponent-total", String.valueOf(opponent.getAliveParticipants() + opponent.getDeadParticipants().size())),
                            Placeholder.unparsed("opponent-players", opponent.getTeamNames()),
                            Placeholder.unparsed("opponent-points", String.valueOf(opponent.getPoints()))
                    );
                }
                placeholders = TagResolver.resolver(placeholders,
                    Placeholder.parsed("red-bed-broken", red.getBedMessage()),
                    Placeholder.parsed("blue-bed-broken", blue.getBedMessage()),
                    Placeholder.unparsed("red-players", red.getTeamNames()),
                    Placeholder.unparsed("red-alive", String.valueOf(red.getAliveParticipants())),
                    Placeholder.unparsed("red-dead", String.valueOf(red.getDeadParticipants().size())),
                    Placeholder.unparsed("red-total", String.valueOf(red.getAliveParticipants() + red.getDeadParticipants().size())),
                    Placeholder.unparsed("red-points", String.valueOf(red.getPoints())),
                    Placeholder.unparsed("blue-players", blue.getTeamNames()),
                    Placeholder.unparsed("blue-alive", String.valueOf(blue.getAliveParticipants())),
                    Placeholder.unparsed("blue-dead", String.valueOf(blue.getDeadParticipants().size())),
                    Placeholder.unparsed("blue-total", String.valueOf(blue.getAliveParticipants() + blue.getDeadParticipants().size())),
                    Placeholder.unparsed("blue-points", String.valueOf(blue.getPoints()))
                );
            }
            if (match instanceof FfaFightMatch ffm) {
                placeholders = TagResolver.resolver(placeholders,
                    Placeholder.unparsed("alive", String.valueOf(ffm.getParticipants().size() - ffm.getDeadParticipants().size())),
                    Placeholder.unparsed("max", String.valueOf(ffm.getParticipants().size())),
                    Placeholder.unparsed("dead", String.valueOf(ffm.getDeadParticipants().size()))
                );
            }
        }
        return placeholders;
    }
    public TagResolver getSettingsResolvers(Profile profile) {
        SettingData settings = profile.getSettingData();
        return TagResolver.resolver(Placeholder.unparsed("max-ping", String.valueOf(settings.getMaxPing())),
                Placeholder.parsed("kill-effect", settings.getKillEffect().getDisplayName()),
                Placeholder.parsed("kill-message", settings.getKillMessagePackage().getDisplayName()),
                Placeholder.parsed("armor-trim", settings.getArmorTrimPackage().getDisplayName()),
                Placeholder.parsed("shield-pattern", settings.getShieldPatternPackage().getDisplayName())
        );
    }
}