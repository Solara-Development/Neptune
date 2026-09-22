package dev.lrxh.api.match;

import dev.lrxh.api.arena.IArena;
import dev.lrxh.api.kit.IKit;
import dev.lrxh.api.match.participant.IParticipant;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface IMatch {

    List<UUID> getSpectators();

    UUID getUuid();

    IMatchState getState();

    IArena getArena();

    IKit getKit();

    List<IParticipant> getParticipants();

    int getRounds();

    int getCurrentRound();

    boolean isDuel();

    boolean isEnded();

    IParticipant getParticipant(Player player);

    IParticipant getParticipant(UUID playerUUID);

    boolean isSpectator(UUID playerUUID);

    void broadcast(String message);

    String getWinnerName();

    String getLoserName();
}
