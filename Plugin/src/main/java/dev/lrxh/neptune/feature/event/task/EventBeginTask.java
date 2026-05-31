package dev.lrxh.neptune.feature.event.task;

import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.configs.impl.SettingsLocale;
import dev.lrxh.neptune.feature.event.AutomatedEvent;
import dev.lrxh.neptune.feature.event.EventService;
import dev.lrxh.neptune.feature.event.EventState;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;

public class EventBeginTask extends NeptuneRunnable {
    private final EventService service;
    private int countdown;

    public EventBeginTask(EventService service) {
        this.service = service;
        this.countdown = 30;
    }

    @Override
    public void run() {
        AutomatedEvent event = service.getActiveEvent();
        if (event == null || event.getState() != EventState.WAITING) {
            stop();
            return;
        }

        if (countdown <= 0) {
            begin(event);
            stop();
            return;
        }

        // Send action bar to all participants
        String actionBar = MessagesLocale.EVENT_ACTION_BAR.getString()
                .replace("<type>", event.getType().name())
                .replace("<time>", String.valueOf(countdown))
                .replace("<players>", String.valueOf(event.getParticipants().size()));

        for (java.util.UUID uuid : event.getParticipants()) {
            var player = Bukkit.getPlayer(uuid);
            if (player != null) player.sendActionBar(CC.color(actionBar));
        }

        countdown--;
    }

    public void forceStart() {
        countdown = 0;
    }

    private void begin(AutomatedEvent event) {
        if (event.getParticipants().size() < SettingsLocale.EVENT_MIN_PLAYERS.getInt()) {
            service.cancelEvent();
            return;
        }
        event.setState(EventState.ACTIVE);
        String playerCount = String.valueOf(event.getParticipants().size());
        Bukkit.getOnlinePlayers().forEach(p ->
                MessagesLocale.EVENT_STARTED.send(p.getUniqueId(), Placeholder.unparsed("players", playerCount)));
        event.getType().start(service);
    }
}
