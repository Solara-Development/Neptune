package dev.lrxh.neptune.feature.queue.tasks;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Queue;

public class QueueMessageTask extends NeptuneRunnable {
    @Override
    public void run() {
        if (!MessagesLocale.QUEUE_REPEAT_TOGGLE.getBoolean()) return;

        for (Queue<QueueEntry> queue : QueueService.get().getAllQueues().values()) {
            for (QueueEntry queueEntry : queue) {
                Profile profile = API.getProfile(queueEntry.getUuid());
                MessagesLocale.QUEUE_REPEAT.send(queueEntry.getUuid(), TagResolver.resolver(
                        Placeholder.parsed("kit", queueEntry.getKit().getDisplayName()),
                        Placeholder.unparsed("max-ping", String.valueOf(profile.getSettingData().getMaxPing()))));
            }
        }
    }
}
