package dev.lrxh.neptune.feature.event.listener;

import dev.lrxh.api.events.MatchEndEvent;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.event.AutomatedEvent;
import dev.lrxh.neptune.feature.event.EventService;
import dev.lrxh.neptune.feature.event.EventState;
import dev.lrxh.neptune.feature.event.EventType;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.game.match.impl.solo.SoloFightMatch;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class EventListener implements Listener {

    @EventHandler
    public void onMatchEnd(MatchEndEvent event) {
        AutomatedEvent activeEvent = EventService.get().getActiveEvent();
        if (activeEvent == null || activeEvent.getState() != EventState.ACTIVE) return;
        if (activeEvent.getType() != EventType.TOURNAMENT) return;
        if (!(event.getMatch() instanceof SoloFightMatch solo)) return;

        // Check if this match involves event participants
        Participant winner = solo.getWinner();
        Participant loser = solo.getLoser();
        if (winner == null || loser == null) return;

        UUID winnerUUID = winner.getPlayerUUID();
        UUID loserUUID = loser.getPlayerUUID();

        if (!activeEvent.getParticipants().contains(winnerUUID) &&
                !activeEvent.getParticipants().contains(loserUUID)) return;

        // Broadcast round result
        TagResolver resolver = TagResolver.resolver(
                Placeholder.unparsed("winner", winner.getNameUnColored()),
                Placeholder.unparsed("loser", loser.getNameUnColored()),
                Placeholder.unparsed("round", String.valueOf(activeEvent.getCurrentRound()))
        );
        for (UUID uuid : activeEvent.getParticipants()) {
            MessagesLocale.EVENT_ROUND_RESULT.send(uuid, resolver);
        }

        activeEvent.getNextRoundPlayers().add(winnerUUID);
        activeEvent.setActiveMatches(activeEvent.getActiveMatches() - 1);

        if (activeEvent.getActiveMatches() <= 0) {
            if (activeEvent.getNextRoundPlayers().size() == 1) {
                EventService.get().endEvent(activeEvent.getNextRoundPlayers().get(0));
            } else {
                activeEvent.setRemainingPlayers(new java.util.ArrayList<>(activeEvent.getNextRoundPlayers()));
                activeEvent.setCurrentRound(activeEvent.getCurrentRound() + 1);
                activeEvent.nextRound(EventService.get());
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        AutomatedEvent activeEvent = EventService.get().getActiveEvent();
        if (activeEvent == null) return;

        UUID uuid = event.getPlayer().getUniqueId();
        if (!activeEvent.getParticipants().contains(uuid)) return;

        activeEvent.getParticipants().remove(uuid);
        activeEvent.getRemainingPlayers().remove(uuid);
        activeEvent.getNextRoundPlayers().remove(uuid);

        if (activeEvent.getState() == EventState.WAITING &&
                activeEvent.getParticipants().size() < dev.lrxh.neptune.configs.impl.SettingsLocale.EVENT_MIN_PLAYERS.getInt()) {
            EventService.get().cancelEvent();
        }
    }
}
