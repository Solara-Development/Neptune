package dev.lrxh.neptune.game.match.impl.team;

import dev.lrxh.api.events.TeamMatchBedDestroyEvent;
import dev.lrxh.api.match.ITeamFightMatch;
import dev.lrxh.api.match.participant.IParticipant;
import dev.lrxh.api.match.team.IMatchTeam;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SoundsLocale;
import dev.lrxh.neptune.game.arena.VirtualArena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.game.match.tasks.MatchRespawnRunnable;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class TeamFightMatch extends Match implements ITeamFightMatch {

    private final MatchTeam teamA;
    private final MatchTeam teamB;

    public TeamFightMatch(VirtualArena arena, Kit kit, List<Participant> participants,
                          MatchTeam teamA, MatchTeam teamB) {
        super(MatchState.STARTING, arena, kit, participants, 1, 1, true, false);
        this.teamA = teamA;
        this.teamB = teamB;
    }

    public MatchTeam getParticipantTeam(Participant participant) {
        return teamA.participants().contains(participant) ? teamA : teamB;
    }

    public IMatchTeam getParticipantTeam(IParticipant participant) {
        return getParticipantTeam((Participant) participant);
    }

    public IMatchTeam getWinner() {
        return teamA.isLoser() ? teamB : teamA;
    }

    public IMatchTeam getLoser() {
        return teamA.isLoser() ? teamA : teamB;
    }

    @Override
    public void win(Participant winner) {
        setState(MatchState.ENDING);

        new MatchEndRunnable(this).start(0L, 20L);
    }

    @Override
    public void end(Participant loser) {
        setState(MatchState.ENDING);
        MatchTeam winnerTeam = teamA.isLoser() ? teamB : teamA;
        MatchTeam loserTeam = getParticipantTeam(loser);


        winnerTeam.sendTitle(CC.color(MessagesLocale.MATCH_WINNER_TITLE_HEADER.getString()),
                CC.color(MessagesLocale.MATCH_WINNER_TITLE_FOOTER.getString().replace("<player>", MessagesLocale.MATCH_YOU.getString())), 100);

        loserTeam.sendTitle(CC.color(MessagesLocale.MATCH_LOSER_TITLE_HEADER.getString()),
                CC.color(MessagesLocale.MATCH_LOSER_TITLE_FOOTER.getString().replace("<player>", MessagesLocale.MATCH_OPPONENT_TEAM.getString())), 100);

        loser.playKillEffect();

        new MatchEndRunnable(this).start(0L, 20L);
    }


    @Override
    public void sendEndMessage() {
        MatchTeam winnerTeam = teamA.isLoser() ? teamB : teamA;
        MatchTeam loserTeam = teamA.isLoser() ? teamA : teamB;

        forEachParticipant(participant -> MessagesLocale.MATCH_END_DETAILS_TEAM.send(participant.getPlayerUUID(),
                new Replacement("<losers>", loserTeam.getTeamNames()),
                new Replacement("<kit>", getKit().getDisplayName()),
                new Replacement("<winners_points>", String.valueOf(winnerTeam.getPoints())),
                new Replacement("<losers_points>", String.valueOf(loserTeam.getPoints())),
                new Replacement("<winners>", winnerTeam.getTeamNames())));
    }


    @Override
    public void breakBed(Participant participant, Participant breaker) {
        MatchTeam team = getParticipantTeam(participant);
        team.setBedBroken(true);
        playSound(SoundsLocale.getSound(SoundsLocale.BED_BROKEN));
        TeamMatchBedDestroyEvent event = new TeamMatchBedDestroyEvent(this, team, breaker);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void sendTitle(Participant participant, TextComponent header, TextComponent footer, int duration) {
        getParticipantTeam(participant).sendTitle(header, footer, duration);
    }

    @Override
    public void onDeath(Participant participant) {
        if (isEnded()) return;
        hideParticipant(participant);
        incrementDeaths(participant);
        participant.setDead(true);

        MatchTeam team = getParticipantTeam(participant);
        team.deadParticipants().add(participant);
        if (!participant.isDisconnected() && !participant.isLeft()) {
            if (getKit().is(KitRule.BED_WARS)) {
                if (!participant.isBedBroken()) {
                    new MatchRespawnRunnable(this, participant).start(0L, 20L);
                    return;
                }
            }

            addSpectator(participant.getPlayer(), participant.getPlayer(), false, false);

            if (participant.getLastAttacker() != null) {
                participant.getLastAttacker().playSound(SoundsLocale.getSound(SoundsLocale.PLAYER_KILL));
            }

            sendDeathMessage(participant);
        }

        // Only end match if everyone on the team is out
        boolean allOut = team.participants().stream()
                .allMatch(p -> p.isDead() || p.isDisconnected() || p.isLeft());

        if (allOut) {
            this.setEnded(true);
            PlayerUtil.doVelocityChange(participant.getPlayerUUID());
            end(participant);
        }
    }

    public boolean onSameTeam(UUID playerUUID, UUID otherUUID) {
        Participant participant = getParticipant(playerUUID);
        Participant other = getParticipant(otherUUID);

        return getParticipantTeam(participant).equals(getParticipantTeam(other));
    }

    @Override
    public void onLeave(Participant participant, boolean quit) {
        if (isEnded()) return;

        participant.setDeathCause(DeathCause.DISCONNECT);
        sendDeathMessage(participant);
        if (quit) {
            participant.setDisconnected(true);
        } else {
            participant.setLeft(true);
            PlayerUtil.reset(participant.getPlayer());
            PlayerUtil.teleportToSpawn(participant.getPlayerUUID());
            Profile profile = API.getProfile(participant.getPlayerUUID());
            profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);
            profile.setMatch(null);
        }

        onDeath(participant);
    }

    @Override
    public void startMatch() {
        setState(MatchState.IN_ROUND);
        showPlayerForSpectators();
        playSound(SoundsLocale.getSound(SoundsLocale.MATCH_START));
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE_FOOTER.getString()), CC.color(MessagesLocale.MATCH_START_TITLE_FOOTER.getString()), 10);
    }

    public String getWinnerName() {
        return getWinner().getTeamNames();
    }

    public String getLoserName() {
        return getLoser().getTeamNames();
    }
}