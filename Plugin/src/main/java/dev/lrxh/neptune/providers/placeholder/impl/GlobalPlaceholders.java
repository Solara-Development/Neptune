package dev.lrxh.neptune.providers.placeholder.impl;

import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.providers.placeholder.PAPIPlaceholder;
import org.bukkit.OfflinePlayer;

public class GlobalPlaceholders implements PAPIPlaceholder {
    @Override
    public boolean match(String string) {
        return true;
    }

    @Override
    public String parse(OfflinePlayer player, String string) {
        switch (string) {
            case "queued" -> { return String.valueOf(QueueService.get().getQueueSize()); }
            case "matches" -> { return String.valueOf(MatchService.get().matches.size()); }
        }
        return null;
    }
}
