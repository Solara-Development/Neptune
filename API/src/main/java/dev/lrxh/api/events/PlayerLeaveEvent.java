package dev.lrxh.api.events;

import dev.lrxh.api.match.IMatch;
import dev.lrxh.api.profile.IProfile;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final IProfile profile;
    @Getter
    private final String previousStatus;

    public PlayerLeaveEvent(IProfile profile, String previousStatus) {
        this.profile = profile;
        this.previousStatus = previousStatus;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
