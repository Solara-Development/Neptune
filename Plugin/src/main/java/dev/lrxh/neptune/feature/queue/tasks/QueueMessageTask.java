package dev.lrxh.neptune.feature.queue.tasks;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class QueueMessageTask extends NeptuneRunnable {
    @Override
    public void run() {
        if (!MessagesLocale.QUEUE_REPEAT_TOGGLE.getBoolean()) return;

        // Track which players have already received the message
        Set<UUID> messageSent = new HashSet<>();

        for (Queue<QueueEntry> queue : QueueService.get().getAllQueues().values()) {
            for (QueueEntry queueEntry : queue) {
                UUID playerUUID = queueEntry.getUuid();
                
                // Skip if we've already sent this player the message
                if (messageSent.contains(playerUUID)) {
                    continue;
                }
                
                Profile profile = API.getProfile(playerUUID);
                MessagesLocale.QUEUE_REPEAT.send(playerUUID, TagResolver.resolver(
                        Placeholder.parsed("kit", queueEntry.getKit().getDisplayName()),
                        Placeholder.unparsed("max-ping", String.valueOf(profile.getSettingData().getMaxPing()))));
                
                // Mark this player as having received the message
                messageSent.add(playerUUID);
            }
        }
    }
}
