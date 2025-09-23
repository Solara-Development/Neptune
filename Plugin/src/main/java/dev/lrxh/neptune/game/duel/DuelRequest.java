package dev.lrxh.neptune.game.duel;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.request.Request;
import dev.lrxh.neptune.utils.CC;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
public class DuelRequest extends Request {
    private final Kit kit;
    private final Arena arena;
    private final boolean party;
    private final int rounds;

    public DuelRequest(UUID sender, Kit kit, Arena arena, boolean party, int rounds) {
        super(sender);
        this.kit = kit;
        this.arena = arena;
        this.party = party;
        this.rounds = rounds;
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

        List<Participant> participants = Arrays.asList(participant1, participant2);

        MatchService.get().startMatch(participants, kit,
                arena, true, rounds);
    }

    public void partyDuel(UUID receiver) {
        Profile receiverProfile = API.getProfile(receiver);
        Profile senderProfile = API.getProfile(getSender());

        List<Participant> participants = new ArrayList<>();

        List<Participant> teamAList = new ArrayList<>();

        for (UUID userUUID : receiverProfile.getGameData().getParty().getUsers()) {
            Player player = Bukkit.getPlayer(userUUID);
            if (player == null)
                continue;

            Participant participant = new Participant(player);
            teamAList.add(participant);
            participants.add(participant);
        }

        List<Participant> teamBList = new ArrayList<>();

        for (UUID userUUID : senderProfile.getGameData().getParty().getUsers()) {
            Player player = Bukkit.getPlayer(userUUID);
            if (player == null)
                continue;

            Participant participant = new Participant(player);
            teamBList.add(participant);
            participants.add(participant);
        }

        MatchTeam teamA = new MatchTeam(teamAList);
        MatchTeam teamB = new MatchTeam(teamBList);

        if (arena == null) {

            for (Participant participant : participants) {
                participant.sendMessage(CC.error("No arenas were found!"));
            }
            return;
        }

        if (!arena.isSetup() || !arena.isDoneLoading()) {

            for (Participant participant : participants) {
                participant.sendMessage(
                        CC.error("Arena wasn't setup up properly! Please contact an admin if you see this."));
            }
            return;
        }

        Bukkit.getScheduler().runTask(Neptune.get(), () -> {
            MatchService.get().startMatch(participants, kit, arena, false,
                    kit.is(KitRule.BEST_OF_THREE) ? 3 : 1);
        });
    }
}
