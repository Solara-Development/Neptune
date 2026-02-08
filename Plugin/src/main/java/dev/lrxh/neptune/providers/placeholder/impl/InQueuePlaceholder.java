package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class InQueuePlaceholder implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.equals("in-queue");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        QueueEntry entry = QueueService.get().get(player.getUniqueId());
        return entry != null ? entry.getKit().getDisplayName() : "";
    }
}
