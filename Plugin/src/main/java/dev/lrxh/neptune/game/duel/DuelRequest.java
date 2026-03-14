package dev.lrxh.neptune.game.duel;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.arena.VirtualArena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.team.MatchTeam;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.providers.request.Request;
import dev.lrxh.neptune.utils.CC;
import lombok.Getter;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
        teamA.setOpponentTeam(teamB);
        teamB.setOpponentTeam(teamA);

        if (arena == null) {
            for (Participant participant : participants) {
                MessagesLocale.QUEUE_NO_ARENAS.send(participant.getPlayer());
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

        Bukkit.getScheduler().runTask(Neptune.get(), () -> MatchService.get().startMatch(teamA, teamB, kit, arena));
    }

    public void sendReceiverMessage(UUID receiverUUID, boolean rematch) {
        Player sender = Bukkit.getPlayer(getSender());
        if (sender == null) return;
        (rematch ? MessagesLocale.REMATCH_REQUEST_RECEIVER : MessagesLocale.DUEL_REQUEST_RECEIVER).send(receiverUUID, TagResolver.resolver(
                Placeholder.parsed("kit", getKit().getDisplayName()),
                Placeholder.parsed("arena", getArena().getDisplayName()),
                Placeholder.unparsed("kit-name", getKit().getName()),
                Placeholder.unparsed("uuid", getSender().toString()),
                Placeholder.unparsed("sender", sender.getName()),
                Placeholder.unparsed("rounds", String.valueOf(getRounds())),
                TagResolver.resolver("accept", Tag.styling(ClickEvent.runCommand("/duel accept-uuid " + getSender()))),
                TagResolver.resolver("deny", Tag.styling(ClickEvent.runCommand("/duel deny-uuid " + getSender())))
        ));
    }
    public void sendSenderMessage(UUID receiverUUID, boolean rematch) {
        Player receiver = Bukkit.getPlayer(receiverUUID);
        if (receiver == null) return;
        (rematch ? MessagesLocale.REMATCH_REQUEST_SENDER : MessagesLocale.DUEL_REQUEST_SENDER).send(getSender(), TagResolver.resolver(
                Placeholder.parsed("kit", getKit().getDisplayName()),
                Placeholder.parsed("arena", getArena().getDisplayName()),
                Placeholder.unparsed("receiver", receiver.getName()),
                Placeholder.unparsed("rounds", String.valueOf(getRounds()))
        ));
    }
}
