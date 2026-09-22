package dev.lrxh.api.queue;

import dev.lrxh.api.kit.IKit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public interface IQueueService {
    int getQueueSize();

    Map<IKit, Queue<IQueueEntry>> getQueues();

    IQueueEntry remove(UUID playerUUID);

    IQueueEntry get(UUID playerUUID);
    IQueueEntry get(UUID playerUUID, IKit kit);

    void addPlayerToQueue(Player player, IKit kit);

    boolean isInQueue(UUID playerUUID);
    boolean isInQueue(UUID playerUUID, IKit kit);
}
