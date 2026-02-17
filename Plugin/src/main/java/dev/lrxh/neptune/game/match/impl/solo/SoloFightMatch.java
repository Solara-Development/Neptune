package dev.lrxh.neptune.game.match.impl.solo;

import dev.lrxh.api.events.SoloMatchBedDestroyEvent;
import dev.lrxh.api.match.ISoloFightMatch;
import dev.lrxh.api.match.participant.IParticipant;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.configs.impl.SoundsLocale;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.feature.leaderboard.LeaderboardService;
import dev.lrxh.neptune.feature.leaderboard.impl.LeaderboardPlayerEntry;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.Match;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.participant.DeathCause;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.game.match.tasks.MatchEndRunnable;
import dev.lrxh.neptune.game.match.tasks.MatchRespawnRunnable;
import dev.lrxh.neptune.game.match.tasks.MatchSecondRoundRunnable;
import dev.lrxh.neptune.profile.data.GameData;
import dev.lrxh.neptune.profile.data.MatchHistory;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.DateUtils;
import dev.lrxh.neptune.utils.PlayerUtil;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.Bukkit;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class SoloFightMatch extends Match implements ISoloFightMatch {

    private final Participant participantA;
    private final Participant participantB;

    public SoloFightMatch(Arena arena, Kit kit, boolean duel, List<Participant> participants, Participant participantA,
                          Participant participantB, int rounds) {
        super(MatchState.STARTING, arena, kit, participants, rounds, 1, duel, false);
        this.participantA = participantA;
        this.participantB = participantB;
    }

    @Override
    public void win(Participant winner) {
        Participant loser = participantA == winner ? participantB : participantA;
        setState(MatchState.ENDING);
        loser.setLoser(true);

        removePlaying();

        new MatchEndRunnable(this).start(0L, 20L);
    }

    @Override
    public void end(Participant loser) {
        setState(MatchState.ENDING);
        loser.setLoser(true);
        Participant winner = getWinner();

        winner.sendTitle(CC.color(MessagesLocale.MATCH_WINNER_TITLE_HEADER.getString()),
                CC.color(MessagesLocale.MATCH_WINNER_TITLE_FOOTER.getString().replace("<player>",
                        MessagesLocale.MATCH_YOU.getString())),
                100);

        if (!loser.isLeft() && !loser.isDisconnected())
            loser.sendTitle(CC.color(MessagesLocale.MATCH_LOSER_TITLE_HEADER.getString()),
                    CC.color(MessagesLocale.MATCH_LOSER_TITLE_FOOTER.getString().replace("<player>",
                            winner.getNameUnColored())),
                    100);

        if (!isDuel()) {
            addStats();
            for (String command : SettingsLocale.COMMANDS_AFTER_MATCH_LOSER.getStringList()) {
                if (command.equals("NONE"))
                    continue;
                command = command.replace("<player>", loser.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }

            for (String command : SettingsLocale.COMMANDS_AFTER_MATCH_WINNER.getStringList()) {
                if (command.equals("NONE"))
                    continue;
                command = command.replace("<player>", winner.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            }

            forEachPlayer(player -> HotbarService.get().giveItems(player));
        }

        removePlaying();

        new MatchEndRunnable(this).start(0L, 20L);
    }

    private void removePlaying() {
        getParticipants().forEach((i) -> getKit().removePlaying());
    }

    public void addStats() {
        Participant winner = getWinner();
        Participant loser = getLoser();

        Profile winnerProfile = API.getProfile(winner.getPlayerUUID());
        Profile loserProfile = API.getProfile(loser.getPlayerUUID());

        String kitName = getKit().getDisplayName();
        String arenaName = getArena().getDisplayName();
        String date = DateUtils.getDate();
        String time = LocalDateTime.now().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        winnerProfile.getGameData().addHistory(
                new MatchHistory(true, loserProfile.getUsername(), kitName, arenaName, date, time));
        loserProfile.getGameData().addHistory(
                new MatchHistory(false, winnerProfile.getUsername(), kitName, arenaName, date, time));

        GameData winnerData = winnerProfile.getGameData();
        GameData loserData = loserProfile.getGameData();

        int initialWinnerElo = winnerData.get(getKit()).getElo();
        int initialLoserElo = loserData.get(getKit()).getElo();

        boolean rankedUp = winnerData.run(getKit(), true);
        loserData.run(getKit(), false);

        winner.setEloChange(winnerData.get(getKit()).getElo() - initialWinnerElo);
        loser.setEloChange(loserData.get(getKit()).getElo() - initialLoserElo);

        if (rankedUp) {
            String divisionName = winnerData.get(getKit()).getDivision().getDisplayName();

            winner.sendTitle(
                    CC.color(MessagesLocale.RANKUP_TITLE_HEADER.getString().replace("<division>", divisionName)),
                    CC.color(MessagesLocale.RANKUP_TITLE_FOOTER.getString().replace("<division>", divisionName)),
                    40);

            winner.sendMessage(MessagesLocale.RANKUP_MESSAGE, Placeholder.parsed("division", divisionName));
        }

        forEachParticipantForce(participant -> LeaderboardService.get().addChange(
                new LeaderboardPlayerEntry(participant.getNameUnColored(), participant.getPlayerUUID(), getKit())));

        if (winnerProfile.isFake())
            Profile.save(winnerProfile);
        if (loserProfile.isFake())
            Profile.save(loserProfile);
    }

    public Participant getLoser() {
        return participantA.isLoser() ? participantA : participantB;
    }

    public Participant getWinner() {
        return participantA.isLoser() ? participantB : participantA;
    }

    @Override
    public void sendEndMessage() {
        Participant winner = getWinner();
        Participant loser = getLoser();

        TagResolver resolver = TagResolver.resolver(
                Placeholder.parsed("kit", getKit().getDisplayName()),
                Placeholder.unparsed("loser", loser.getNameUnColored()),
                Placeholder.unparsed("winner-points", String.valueOf(winner.getPoints())),
                Placeholder.unparsed("loser-points", String.valueOf(loser.getPoints())),
                Placeholder.unparsed("winner", winner.getNameUnColored())
        );

        if (!isDuel()) {
            resolver = TagResolver.resolver(resolver,
                Placeholder.unparsed("winner-elo", String.valueOf(winner.getEloChange())),
                Placeholder.unparsed("loser-elo", String.valueOf(loser.getEloChange())
            ));
            broadcast(MessagesLocale.MATCH_END_DETAILS_SOLO, resolver);
        } else {
            broadcast(MessagesLocale.MATCH_END_DETAILS_DUEL, resolver);
        }

        forEachParticipant(participant -> {
            if (MessagesLocale.MATCH_PLAY_AGAIN_ENABLED.getBoolean()) {
                PlayerUtil.sendMessage(participant.getPlayerUUID(), MessagesLocale.MATCH_PLAY_AGAIN.getString(), TagResolver.resolver(
                        Placeholder.parsed("kit", getKit().getDisplayName()),
                        Placeholder.unparsed("kit-name", getKit().getName()),
                        TagResolver.resolver("play-again", Tag.styling(ClickEvent.runCommand("/queue " + getKit().getName())))
                ));
            }
        });
    }

    @Override
    public void breakBed(Participant participant, Participant breaker) {
        participant.setBedBroken(true);
        playSound(SoundsLocale.getSound(SoundsLocale.BED_BROKEN));
        SoloMatchBedDestroyEvent event = new SoloMatchBedDestroyEvent(this, participant, breaker);
        Bukkit.getPluginManager().callEvent(event);
    }

    @Override
    public void sendTitle(Participant participant, TextComponent header, TextComponent footer, int duration) {
        participant.sendTitle(header, footer, duration);
    }

    @Override
    public void onDeath(Participant participant) {
        if (isEnded())
            return;
        incrementDeaths(participant);
        hideParticipant(participant);

        participant.setDead(true);

        playSound(SoundsLocale.getSound(SoundsLocale.MATCH_PARTICIPANT_DIED));

        Participant participantKiller = participantA.getNameColored().equals(participant.getNameColored())
                ? participantB
                : participantA;
        sendDeathMessage(participant);

        if (!participant.isDisconnected() && !participant.isLeft()) {
            if (getKit().is(KitRule.BED_WARS)) {
                if (!participant.isBedBroken()) {
                    participantKiller.setCombo(0);
                    new MatchRespawnRunnable(this, participant).start(0L, 20L);
                    return;
                }
            }

            if (getRounds() > 1) {
                participantKiller.addWin();
                if (participantKiller.getPoints() < getRounds()) {
                    participantKiller.setCombo(0);

                    setState(MatchState.STARTING);
                    new MatchSecondRoundRunnable(this, participant).start(0L, 20L);
                    return;
                }
            }
        }

        if (participant.getLastAttacker() != null) {
            participant.getLastAttacker().playSound(SoundsLocale.getSound(SoundsLocale.PLAYER_KILL));
        }

        this.setEnded(true);

        participant.playKillEffect();
        PlayerUtil.doVelocityChange(participant.getPlayerUUID());

        if (!participant.isDisconnected() && !participant.isLeft() && !isEnded()) {
            Participant otherParticipant = participantA.equals(participant) ? participantB : participantA;
            if (otherParticipant.getPlayer() != null) {
                addSpectator(participant.getPlayer(), otherParticipant.getPlayer(), false, false);
            }
        }

        end(participant);
    }

    @Override
    public void onLeave(Participant participant, boolean quit) {
        if (isEnded())
            return;

        participant.setDeathCause(DeathCause.DISCONNECT);
        sendDeathMessage(participant);
        setEnded(true);
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

        end(participant);
    }

    @Override
    public void startMatch() {
        setState(MatchState.IN_ROUND);
        showPlayerForSpectators();
        playSound(SoundsLocale.getSound(SoundsLocale.MATCH_START));
        sendTitle(CC.color(MessagesLocale.MATCH_START_TITLE_HEADER.getString()),
                CC.color(MessagesLocale.MATCH_START_TITLE_FOOTER.getString()), 20);
    }

    @Override
    public List<IParticipant> getParticipants() {
        return List.of(participantA, participantB);
    }

    public Participant getRedParticipant() {
        return participantA.getColor().equals(ParticipantColor.RED) ? participantA : participantB;
    }

    public Participant getBlueParticipant() {
        return participantA.getColor().equals(ParticipantColor.BLUE) ? participantA : participantB;
    }

    public String getWinnerName() {
        return getWinner() != null ? getWinner().getName() : "";
    }
    public String getLoserName() {
        return getLoser() != null ? getLoser().getName() : "";
    }
}