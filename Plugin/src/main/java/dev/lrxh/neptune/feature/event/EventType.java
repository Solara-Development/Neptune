package dev.lrxh.neptune.feature.event;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum EventType {

    TOURNAMENT(MenusLocale.EVENT_TYPE_TOURNAMENT_SLOT.getInt()) {
        @Override
        public void start(EventService service) {
            AutomatedEvent event = service.getActiveEvent();
            event.setRemainingPlayers(new java.util.ArrayList<>(event.getParticipants()));
            event.setCurrentRound(1);
            event.nextRound(service);
        }
    },
    LMS(MenusLocale.EVENT_TYPE_LMS_SLOT.getInt()) {
        @Override
        public void start(EventService service) {
            AutomatedEvent event = service.getActiveEvent();
            List<Participant> participants = event.getParticipants().stream()
                    .map(uuid -> new Participant(Bukkit.getPlayer(uuid)))
                    .filter(p -> p.getPlayer() != null)
                    .collect(Collectors.toList());
            event.getKit().getRandomArena().thenAccept(arena -> {
                if (arena == null) {
                    service.stopEvent();
                    return;
                }
                Bukkit.getScheduler().runTask(Neptune.get(), () ->
                        MatchService.get().startMatch(participants, event.getKit(), arena));
            });
        }
    };

    private final int slot;

    EventType(int slot) {
        this.slot = slot;
    }

    public abstract void start(EventService service);
}
