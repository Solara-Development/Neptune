package dev.lrxh.neptune.game.match;

import dev.lrxh.api.events.MatchReadyEvent;
import dev.lrxh.api.match.IMatch;
import dev.lrxh.api.match.IMatchService;
import dev.lrxh.api.match.participant.IParticipant;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.match.impl.ffa.FfaFightMatch;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.game.match.impl.team.TeamFightMatch;
import dev.lrxh.neptune.game.match.tasks.MatchStartRunnable;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class MatchService implements IMatchService {
    private static MatchService instance;
    public final HashSet<Match> matches = new HashSet<>();

    public static MatchService get() {
        if (instance == null)
            instance = new MatchService();

        return instance;
    }

    /**
     * Resolve parent to a copied instance and reserve it atomically. Only copies are used; original (parent) is never played on.
     */
    private Arena resolveAndReserveArena(Arena arena) {
        synchronized (ArenaService.ARENA_SELECTION_LOCK) {
            Arena instance = arena.getAvailableArena();
            if (instance == null) return null;
            instance.setPlaying(true);
            return instance;
        }
    }

    public void startMatch(Participant playerRed, Participant playerBlue, Kit kit, Arena arena, boolean duel, int rounds) {
        if (!Neptune.get().isAllowMatches()) return;

        Arena arenaInstance = resolveAndReserveArena(arena);
        if (arenaInstance == null) return;

        arena = arenaInstance;

        kit.addPlaying();
        kit.addPlaying();

        playerRed.setOpponent(playerBlue);
        playerRed.setColor(ParticipantColor.RED);

        playerBlue.setOpponent(playerRed);
        playerBlue.setColor(ParticipantColor.BLUE);

        SoloFightMatch match = new SoloFightMatch(arena, kit, duel, Arrays.asList(playerRed, playerBlue), playerRed,
                playerBlue, rounds);

        MatchReadyEvent event = new MatchReadyEvent(match);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            arena.setPlaying(false);
            return;
        }
        matches.add(match);
        new MatchStartRunnable(match).start(0L, 20L);
    }

    public void startMatch(MatchTeam teamA, MatchTeam teamB, Kit kit, Arena arena) {
        if (!Neptune.get().isAllowMatches()) return;

        Arena arenaInstance = resolveAndReserveArena(arena);
        if (arenaInstance == null) return;

        arena = arenaInstance;

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

        MatchReadyEvent event = new MatchReadyEvent(match);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            arena.setPlaying(false);
            return;
        }

        matches.add(match);
        new MatchStartRunnable(match).start(0L, 20L);
    }

    public void startMatch(List<Participant> participants, Kit kit, Arena arena) {
        if (!Neptune.get().isAllowMatches()) return;

        Arena arenaInstance = resolveAndReserveArena(arena);
        if (arenaInstance == null) return;

        arena = arenaInstance;

        for (Participant participant : participants) {
            participant.setColor(ParticipantColor.RED);
        }

        FfaFightMatch match = new FfaFightMatch(arena, kit, participants);

        MatchReadyEvent event = new MatchReadyEvent(match);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            arena.setPlaying(false);
            return;
        }

        matches.add(match);
        new MatchStartRunnable(match).start(0L, 20L);
    }

    @Override
    public void startMatch(IMatch match, Player redPlayer, Player bluePlayer) {
        Kit kit = KitService.get().copyFrom(match.getKit());

        kit.getRandomArena().thenAccept(parentArena -> {
            if (parentArena == null) {
                redPlayer.sendMessage(CC.error("No arenas available for rematch."));
                bluePlayer.sendMessage(CC.error("No arenas available for rematch."));
                return;
            }

            Arena arena = resolveAndReserveArena(parentArena);
            if (arena == null) {
                redPlayer.sendMessage(CC.error("No arenas available for rematch."));
                bluePlayer.sendMessage(CC.error("No arenas available for rematch."));
                return;
            }

            MatchReadyEvent event = new MatchReadyEvent(match);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                arena.setPlaying(false);
                return;
            }

            Match neptuneMatch = new SoloFightMatch(
                    arena,
                    kit,
                    true,
                    new ArrayList<>(),
                    new Participant(redPlayer),
                    new Participant(bluePlayer),
                    1);

            matches.add(neptuneMatch);
            new MatchStartRunnable(neptuneMatch).start(0L, 20L);
        });
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

    public void stopAllGames() {
        for (Match match : matches) {
            match.resetArena();
        }
    }
}
