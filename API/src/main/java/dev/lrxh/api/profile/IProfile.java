package dev.lrxh.api.profile;

import dev.lrxh.api.data.IGameData;
import dev.lrxh.api.match.IMatch;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IProfile {
    Player getPlayer();

    UUID getPlayerUUID();

    IGameData getGameData();

    IMatch getIMatch();

    void setState(String customState);

    String getProfileState();

    void toLobby();

    boolean hasState(String state);

    void addCooldown(String name, int millis);

    boolean hasCooldownEnded(String name);

    void forfeit();
}
