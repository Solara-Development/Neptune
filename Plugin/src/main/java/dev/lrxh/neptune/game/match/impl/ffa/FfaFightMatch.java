package dev.lrxh.neptune.game.match.impl.ffa;

import dev.lrxh.api.match.IFffaFightMatch;
import dev.lrxh.api.match.participant.IParticipant;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SoundsLocale;
import dev.lrxh.neptune.game.arena.VirtualArena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import net.kyori.adventure.text.TextComponent;

import java.util.ArrayList;
import java.util.List;

public class FfaFightMatch extends Match implements IFffaFightMatch {
    public final List<Participant> deadParticipants;
    private final List<Participant> participants;
    @Getter
    private Participant winner;

    public FfaFightMatch(VirtualArena arena, Kit kit, List<Participant> participants) {
        super(MatchState.STARTING, arena, kit, participants, 1, 1, true, false);
        this.winner = null;
        this.deadParticipants = new ArrayList<>();
        this.participants = participants;
    }

    @Override
    public void win(Participant winner) {
        setState(MatchState.ENDING);
        this.winner = winner;
        this.setEnded(true);

        new MatchEndRunnable(this).start(0L, 20L);
    }

    @Override
    public void end(Participant loser) {
        setState(MatchState.ENDING);
        loser.setLoser(true);
        forEachParticipant(participant -> {
            if (winner == null) return;
            participant.sendTitle(CC.color(MessagesLocale.MATCH_WINNER_TITLE_HEADER.getString()),
                    CC.color(MessagesLocale.MATCH_WINNER_TITLE_FOOTER.getString().replace("<player>", winner.getNameUnColored())), 100);
        });

        loser.playKillEffect();

        new MatchEndRunnable(this).start(0L, 20L);
    }

    @Override
    public void onDeath(Participant participant) {
        if (isEnded()) return;

        hideParticipant(participant);
        incrementDeaths(participant);
        participant.setDead(true);
        participant.setLoser(true);
        Profile profile = API.getProfile(participant.getPlayerUUID());

        if (!participant.isLeft() && !participant.isDisconnected()) {
            addSpectator(participant.getPlayer(), participant.getPlayer(), false, false);
        } else {
            if (participant.getPlayer() != null) {
                PlayerUtil.reset(participant.getPlayer());
                PlayerUtil.teleportToSpawn(participant.getPlayerUUID());
            }

            if (profile != null) {
                profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);
                profile.setMatch(null);
            }
        }

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(SoundsLocale.getSound(SoundsLocale.PLAYER_KILL));
        }

        sendDeathMessage(participant);
        deadParticipants.add(participant);

        if (!isLastPlayerStanding()) return;

        winner = getLastPlayerStanding();
        setEnded(true);
        end(participant);
    }

    public String getWinnerName() {
        return winner.getName();
    }

    public String getLoserName() {
        return participants.stream().filter(p -> (p != winner)).map(p -> p.getNameColored()).toList().toString();
    }

    private boolean isLastPlayerStanding() {
        return getParticipants().size() - deadParticipants.size() == 1;
    }

    private Participant getLastPlayerStanding() {
        for (IParticipant participant : getParticipants()) {
            if (!deadParticipants.contains(participant)) {
                return (Participant) participant;
            }
        }
        return null;
    }

    @Override
    public void onLeave(Participant participant, boolean quit) {
        if (isEnded()) return;

        participant.setDeathCause(DeathCause.DISCONNECT);
        Profile profile = API.getProfile(participant.getPlayerUUID());

        if (quit) {
            participant.setDisconnected(true);
        } else {
            participant.setLeft(true);
            PlayerUtil.reset(participant.getPlayer());
            PlayerUtil.teleportToSpawn(participant.getPlayerUUID());
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
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE_HEADER.getString()), CC.color(MessagesLocale.MATCH_START_TITLE_FOOTER.getString()), 20);
    }

    @Override
    public void sendEndMessage() {
        if (winner == null) return;
        forEachParticipant(participant -> MessagesLocale.MATCH_END_DETAILS_FFA.send(participant.getPlayerUUID(),
                new Replacement("<winner>", winner.getNameUnColored()),
                new Replacement("<kit>", getKit().getDisplayName())));
    }

    @Override
    public void breakBed(Participant participant, Participant breaker) {
    }

    @Override
    public void sendTitle(Participant participant, TextComponent header, TextComponent footer, int duration) {
        participant.sendTitle(header, footer, duration);
    }

    @Override
    public List<IParticipant> getParticipants() {
        return new ArrayList<>(participants);
    }

    @Override
    public List<IParticipant> getDeadParticipants() {
        return new ArrayList<>(deadParticipants);
    }
}
