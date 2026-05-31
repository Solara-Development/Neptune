package dev.lrxh.neptune.feature.event.command;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.event.EventService;
import dev.lrxh.neptune.feature.event.menu.EventAdminMenu;
import org.bukkit.entity.Player;

public class EventCommand {

    @Command(name = "", desc = "Event command")
    public void base(@Sender Player player) {
        MessagesLocale.EVENT_NOT_ACTIVE.send(player.getUniqueId());
    }

    @Command(name = "join", desc = "Join the active event")
    public void join(@Sender Player player) {
        EventService.get().joinEvent(player.getUniqueId());
    }

    @Command(name = "start", desc = "Start an event")
    @Require("neptune.event.start")
    public void start(@Sender Player player) {
        new EventAdminMenu().open(player);
    }

    @Command(name = "stop", desc = "Stop the active event")
    @Require("neptune.event.start")
    public void stop(@Sender Player player) {
        EventService.get().stopEvent();
    }

    @Command(name = "info", desc = "Show active event info")
    public void info(@Sender Player player) {
        var event = EventService.get().getActiveEvent();
        if (event == null) {
            MessagesLocale.EVENT_NOT_ACTIVE.send(player.getUniqueId());
            return;
        }
        player.sendMessage("§6Event: §e" + event.getType().name()
                + " §7| State: §e" + event.getState().name()
                + " §7| Players: §b" + event.getParticipants().size());
    }
}
