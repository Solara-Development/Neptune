package dev.lrxh.neptune.feature.event;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.participant.ParticipantColor;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AutomatedEvent {
    private EventType type;
    private EventState state;
    private Kit kit;
    private List<UUID> participants;
    private UUID winner;
    private UUID starterUUID;

    // Bracket fields
    private List<UUID> remainingPlayers;
    private List<UUID> nextRoundPlayers;
    private int currentRound;
    private int activeMatches;

    public AutomatedEvent(EventType type, Kit kit) {
        this.type = type;
        this.state = EventState.WAITING;
        this.kit = kit;
        this.participants = new ArrayList<>();
        this.remainingPlayers = new ArrayList<>();
        this.nextRoundPlayers = new ArrayList<>();
        this.currentRound = 0;
        this.activeMatches = 0;
    }

    public boolean isEnded() {
        return state == EventState.ENDED;
    }

    public void nextRound(EventService service) {
        Collections.shuffle(remainingPlayers);
        nextRoundPlayers = new ArrayList<>();
        activeMatches = 0;

        // Odd player out gets a bye
        if (remainingPlayers.size() % 2 != 0) {
            UUID bye = remainingPlayers.remove(remainingPlayers.size() - 1);
            nextRoundPlayers.add(bye);
        }

        for (int i = 0; i < remainingPlayers.size() - 1; i += 2) {
            UUID uuidA = remainingPlayers.get(i);
            UUID uuidB = remainingPlayers.get(i + 1);

            Participant pA = new Participant(Bukkit.getPlayer(uuidA));
            Participant pB = new Participant(Bukkit.getPlayer(uuidB));

            if (pA.getPlayer() == null || pB.getPlayer() == null) {
                // skip disconnected players, advance the one who is online
                if (pA.getPlayer() != null) nextRoundPlayers.add(uuidA);
                if (pB.getPlayer() != null) nextRoundPlayers.add(uuidB);
                continue;
            }

            activeMatches++;
            final int round = currentRound;
            kit.getRandomArena().thenAccept(arena -> {
                if (arena == null) {
                    activeMatches--;
                    return;
                }
                Bukkit.getScheduler().runTask(Neptune.get(), () -> {
                    pA.setOpponent(pB);
                    pA.setColor(ParticipantColor.RED);
                    pB.setOpponent(pA);
                    pB.setColor(ParticipantColor.BLUE);
                    MatchService.get().startMatch(pA, pB, kit, arena, false, 1);
                });
            });
        }

        // Broadcast round start
        for (UUID uuid : participants) {
            MessagesLocale.EVENT_STARTED.send(uuid,
                    Placeholder.unparsed("players", String.valueOf(remainingPlayers.size())));
        }
    }
}
