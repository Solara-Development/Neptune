package dev.lrxh.neptune.game.match;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.api.events.MatchEndEvent;
import dev.lrxh.api.events.MatchReadyEvent;
import dev.lrxh.api.kit.IKit;
import dev.lrxh.api.match.IMatch;
import dev.lrxh.api.match.IMatchService;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.feature.event.AutomatedEvent;
import dev.lrxh.neptune.feature.event.EventService;
import dev.lrxh.neptune.feature.event.EventState;
import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.impl.MatchState;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.game.match.tasks.MatchStartRunnable;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MatchService implements IMatchService {
    private static MatchService instance;
    public final HashSet<Match> matches = new HashSet<>();

    public static MatchService get() {
        if (instance == null) instance = new MatchService();

        return instance;
    }

    public void startMatch(Participant playerRed, Participant playerBlue, Kit kit, IArena arena, boolean duel, int rounds) {
        if (!Neptune.get().isAllowMatches()) {
            arena.remove();
            return;
        }
        kit.addPlaying(2);

        playerRed.setOpponent(playerBlue);
        playerRed.setColor(ParticipantColor.RED);

        playerBlue.setOpponent(playerRed);
        playerBlue.setColor(ParticipantColor.BLUE);

        SoloFightMatch match = new SoloFightMatch(arena, kit, duel, Arrays.asList(playerRed, playerBlue), playerRed, playerBlue, rounds);
        playerRed.setMatch(match);
        playerBlue.setMatch(match);
        MatchReadyEvent event = new MatchReadyEvent(match);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            arena.remove();
            kit.removePlaying(2);
            return;
        }
        matches.add(match);
        new MatchStartRunnable(match).start(0L, 20L);
    }

    public void startMatch(MatchTeam teamA, MatchTeam teamB, Kit kit, IArena arena) {
        if (!Neptune.get().isAllowMatches()) {
            arena.remove();
            return;
        }
        int totalParticipants = teamA.participants().size() + teamB.participants().size();
        kit.addPlaying(totalParticipants);
        for (Participant participant : teamA.participants()) {
            for (Participant opponent : teamB.participants()) {
                participant.setOpponent(opponent);
                participant.setColor(ParticipantColor.RED);
                opponent.setOpponent(participant);
                opponent.setColor(ParticipantColor.BLUE);
            }
        }


        List<Participant> participants = new ArrayList<>(teamA.participants());
        participants.addAll(teamB.participants());

        TeamFightMatch match = new TeamFightMatch(arena, kit, participants, teamA, teamB);
        for (Participant participant : participants) {
            participant.setMatch(match);
        }
        MatchReadyEvent event = new MatchReadyEvent(match);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            arena.remove();
            kit.removePlaying(totalParticipants);
            return;
        }

        matches.add(match);
        new MatchStartRunnable(match).start(0L, 20L);
    }

    public void startMatch(List<Participant> participants, Kit kit, IArena arena) {
        if (!Neptune.get().isAllowMatches()) {
            arena.remove();
            return;
        }
        kit.addPlaying(participants.size());
        for (Participant participant : participants) {
            participant.setColor(ParticipantColor.RED);
        }

        FfaFightMatch match = new FfaFightMatch(arena, kit, participants);

        MatchReadyEvent event = new MatchReadyEvent(match);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            arena.remove();
            kit.removePlaying(participants.size());
            return;
        }

        matches.add(match);
        new MatchStartRunnable(match).start(0L, 20L);
    }

    @Override
    public void startMatch(IMatch match, Player redPlayer, Player bluePlayer) {
        if (!Neptune.get().isAllowMatches()) return;
        MatchReadyEvent event = new MatchReadyEvent(match);

        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        Arena source = ArenaService.get().getArenaByName(match.getArena().getName());
        CompletableFuture<? extends IArena> arenaFuture = (source != null)
                ? source.acquire()
                : ArenaService.get().copyFrom(match.getArena()).acquire();

        arenaFuture.thenAccept(arena -> {
            if (arena == null) return;
            Match neptuneMatch = new SoloFightMatch(
                    arena,
                    KitService.get().copyFrom(match.getKit()),
                    true,
                    new ArrayList<>(),
                    new Participant(redPlayer),
                    new Participant(bluePlayer),
                    1
            );

            matches.add(neptuneMatch);
            new MatchStartRunnable(neptuneMatch).start(0L, 20L);
        });
    }

    @Override
    public void startFfaMatch(List<Player> players, IKit kit) {
        Kit implKit = KitService.get().getKitByName(kit.getName());
        if (implKit == null) return;
        List<Participant> participants = new ArrayList<>();
        for (Player p : players) participants.add(new Participant(p));
        IArena arena = implKit.getRandomArena().join();
        if (arena == null) return;
        startMatch(participants, implKit, arena);
    }

    @Override
    public void startTeamMatch(List<Player> redTeam, List<Player> blueTeam, IKit kit) {
        Kit implKit = KitService.get().getKitByName(kit.getName());
        if (implKit == null) return;
        MatchTeam teamA = new MatchTeam(redTeam.stream().map(Participant::new).collect(Collectors.toList()));
        MatchTeam teamB = new MatchTeam(blueTeam.stream().map(Participant::new).collect(Collectors.toList()));
        teamA.setOpponentTeam(teamB);
        teamB.setOpponentTeam(teamA);
        IArena arena = implKit.getRandomArena().join();
        if (arena == null) return;
        startMatch(teamA, teamB, implKit, arena);
    }

    @Override
    public void startDuelMatch(Player redPlayer, Player bluePlayer, IKit kit, boolean duel, int rounds) {
        Kit implKit = KitService.get().getKitByName(kit.getName());
        if (implKit == null) return;
        IArena arena = implKit.getRandomArena().join();
        if (arena == null) return;
        startMatch(new Participant(redPlayer), new Participant(bluePlayer), implKit, arena, duel, rounds);
    }

    public Optional<Match> getMatch(Player player) {
        Profile profile = API.getProfile(player);
        return Optional.ofNullable(profile)
                .map(Profile::getMatch);
    }

    public Optional<Match> getMatch(UUID uuid) {
        Profile profile = API.getProfile(uuid);
        return Optional.ofNullable(profile)
                .map(Profile::getMatch);
    }

    @Override
    public IMatch getMatchApi(Player player) {
        return getMatch(player).orElse(null);
    }

    @Override
    public IMatch getMatchApi(UUID uuid) {
        return getMatch(uuid).orElse(null);
    }

    @Override
    public List<IMatch> getActiveMatches() {
        return new ArrayList<>(matches);
    }

    @Override
    public void stopMatch(IMatch match) {
        if (!(match instanceof Match m) || !matches.remove(m)) return;
        m.setState(MatchState.ENDING);
        if (m.getKit().is(KitRule.SHOW_HP)) {
            m.hideHealth();
        }

        for (UUID spectator : new HashSet<>(m.spectators)) {
            m.removeSpectator(spectator, false);
        }

        m.resetArena();

        m.forEachParticipant(participant -> {

            Profile profile = API.getProfile(participant.getPlayerUUID());
            if (profile.getMatch() != m) return;

            PlayerUtil.reset(participant.getPlayer());
            profile.setMatch(null);
            PlayerUtil.teleportToSpawn(participant.getPlayerUUID());

            AutomatedEvent activeEvent = EventService.get().getActiveEvent();
            if (activeEvent != null && activeEvent.getState() == EventState.ACTIVE
                    && activeEvent.getParticipants().contains(participant.getPlayerUUID())) {
                profile.setState(ProfileState.IN_EVENT);
            } else {
                profile.setState(profile.getGameData().getParty() == null ? ProfileState.IN_LOBBY : ProfileState.IN_PARTY);
            }

            m.forEachPlayer(player -> HotbarService.get().giveItems(player));
        });

        m.sendEndMessage();
        m.getArena().remove();
        if (m.getArena() instanceof Arena arena) arena.setInUse(false);
        MatchService.get().matches.remove(m);
        MatchEndEvent event = new MatchEndEvent(m);
        Bukkit.getPluginManager().callEvent(event);
        m.setEnded(true);
    }

    public void stopAllGames() {
        for (Match match : matches) {
            match.resetArena();
        }
    }
}
