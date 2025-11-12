package dev.lrxh.neptune.game.duel;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.arena.VirtualArena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.request.Request;
import dev.lrxh.neptune.utils.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import net.kyori.adventure.title.Title;
import java.time.Duration;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class DuelRequest extends Request {
    private final Kit kit;
    private final VirtualArena arena;
    private final boolean party;
    private final int rounds;

    public DuelRequest(UUID sender, Kit kit, VirtualArena arena, boolean party, int rounds) {
        super(sender);
        this.kit = kit;
        this.arena = arena;
        this.party = party;
        this.rounds = rounds;
    }

    private void preloadArena(dev.lrxh.neptune.game.arena.VirtualArena arena) {
        if (arena == null) return;
        Location[] important = new Location[]{arena.getRedSpawn(), arena.getBlueSpawn(), arena.getMin(), arena.getMax()};
        for (Location loc : important) {
            if (loc == null) continue;
            World world = loc.getWorld();
            if (world == null) continue;
            int baseCx = loc.getBlockX() >> 4;
            int baseCz = loc.getBlockZ() >> 4;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dz = -1; dz <= 1; dz++) {
                    world.getChunkAt(baseCx + dx, baseCz + dz).load(true);
                }
            }
        }
    }

    public void start(UUID receiver) {
        if (party) {
            partyDuel(receiver);
        } else {
            normalDuel(receiver);
        }
    }

    public void normalDuel(UUID receiver) {
        Player sender = Bukkit.getPlayer(getSender());
        Player reciverPlayer = Bukkit.getPlayer(receiver);

        if (reciverPlayer == null || sender == null)
            return;

        Participant participant1 = new Participant(sender);

        Participant participant2 = new Participant(reciverPlayer);

        MatchService.get().startMatch(participant1, participant2, kit,
                arena, true, rounds);
    }

    public void partyDuel(UUID receiver) {
        Profile receiverProfile = API.getProfile(receiver);
        Profile senderProfile = API.getProfile(getSender());

        List<Participant> participants = new ArrayList<>();
        List<Player> players = new ArrayList<>();

        List<Participant> teamAList = new ArrayList<>();

        for (UUID userUUID : receiverProfile.getGameData().getParty().getUsers()) {
            Player player = Bukkit.getPlayer(userUUID);
            if (player == null)
                continue;

            Participant participant = new Participant(player);
            teamAList.add(participant);
            participants.add(participant);
            players.add(player);
        }

        List<Participant> teamBList = new ArrayList<>();

        for (UUID userUUID : senderProfile.getGameData().getParty().getUsers()) {
            Player player = Bukkit.getPlayer(userUUID);
            if (player == null)
                continue;

            Participant participant = new Participant(player);
            teamBList.add(participant);
            participants.add(participant);
            players.add(player);
        }

        MatchTeam teamA = new MatchTeam(teamAList);
        MatchTeam teamB = new MatchTeam(teamBList);
        teamA.setOpponentTeam(teamB);
        teamB.setOpponentTeam(teamA);

        if (arena == null) {

            for (Participant participant : participants) {
                participant.sendMessage(CC.error("No arenas were found!"));
            }
            return;
        }

        if (!arena.isSetup()) {

            for (Participant participant : participants) {
                participant.sendMessage(
                        CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
            }
            return;
        }

        // Preload arena chunks so teleport feels instant after delay
        preloadArena(arena);

        // Play accept sound, close inventories, and show titles to all party members
        Sound sound = Sound.ITEM_MACE_SMASH_AIR;
        for (Player p : players) {
            p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
            p.closeInventory();
        }

        partyDuelTitles(players, 100L);

        Bukkit.getScheduler().runTaskLater(Neptune.get(), () -> {
            MatchService.get().startMatch(teamA, teamB, kit, arena);
        }, 100L);
    }

    private void partyDuelTitles(List<Player> players, long delayTicks) {
        String[] titles = {
                "&e⚔ &6Duel Accepted &e⚔",
                "&6⚔ &6Duel Accepted &6⚔",
                "&e⚔ &6Duel Accepted &e⚔",
                "&6⚔ &6Duel Accepted &6⚔"
        };

        String[] subs = {
                "&7Teleporting to arena.",
                "&7Teleporting to arena..",
                "&7Teleporting to arena...",
                "&7Teleporting to arena."
        };
        for (long t = 1L; t < delayTicks; t += 5L) {
            int idx = (int) ((t / 5L) % titles.length);
            Bukkit.getScheduler().runTaskLater(Neptune.get(), () -> {
                Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofMillis(500), Duration.ZERO);
                for (Player p : players) {
                    p.showTitle(Title.title(CC.color(titles[idx]), CC.color(subs[idx]), times));
                }
            }, t);
        }
    }
}
