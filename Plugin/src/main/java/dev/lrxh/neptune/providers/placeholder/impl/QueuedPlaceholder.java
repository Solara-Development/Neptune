package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class QueuedPlaceholder implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return string.equals("queued");
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        return String.valueOf(QueueService.get().getQueueSize());
    }
}
