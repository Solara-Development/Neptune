package dev.lrxh.neptune.feature.queue;

import dev.lrxh.api.events.QueueJoinEvent;
import dev.lrxh.api.kit.IKit;
import dev.lrxh.api.queue.IQueueEntry;
import dev.lrxh.api.queue.IQueueService;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.impl.Profile;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.Bukkit;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

public class QueueService implements IQueueService {

    private static QueueService instance;

    private final Map<Kit, Queue<QueueEntry>> kitQueues = new HashMap<>();

    public static QueueService get() {
        if (instance == null) instance = new QueueService();
        return instance;
    }

    public void add(QueueEntry queueEntry, boolean add) {
        UUID playerUUID = queueEntry.getUuid();
        Kit kit = queueEntry.getKit();

        if (get(playerUUID) != null) return;

        Profile profile = API.getProfile(playerUUID);
        if (!profile.hasState(ProfileState.IN_LOBBY)) return;
        if (profile.getGameData().getParty() != null) return;
        if (queueEntry.getKit().is(KitRule.HIDDEN)) return;

        kitQueues.computeIfAbsent(kit, k -> new ConcurrentLinkedQueue<>()).offer(queueEntry);

        if (add) {
            QueueJoinEvent event = new QueueJoinEvent(queueEntry);
            Bukkit.getScheduler().runTask(Neptune.get(), () -> Bukkit.getPluginManager().callEvent(event));
            if (event.isCancelled()) return;
            profile.setState(ProfileState.IN_QUEUE);
            kit.addQueue();
            MessagesLocale.QUEUE_JOIN.send(playerUUID, TagResolver.resolver(
                    Placeholder.parsed("kit", kit.getDisplayName()),
                    Placeholder.unparsed("max-ping", String.valueOf(profile.getSettingData().getMaxPing()))));
        }
    }

    public QueueEntry remove(UUID playerUUID) {
        QueueEntry entry = get(playerUUID);
        if (entry == null) return null;

        Kit kit = entry.getKit();
        Queue<QueueEntry> queue = kitQueues.get(kit);
        if (queue != null) {
            queue.remove(entry);
            entry.getKit().removeQueue();
        }

        return entry;
    }

    public void remove(QueueEntry queueEntry) {
        remove(queueEntry.getUuid());
    }

    public QueueEntry poll(Kit kit) {
        Queue<QueueEntry> queue = kitQueues.get(kit);
        if (queue == null || queue.isEmpty()) return null;

        List<QueueEntry> entries = new ArrayList<>(queue);
        return remove(entries.get(new Random().nextInt(entries.size())).getUuid());
    }

    public QueueEntry get(UUID uuid) {
        for (Queue<QueueEntry> queue : kitQueues.values()) {
            for (QueueEntry entry : queue) {
                if (entry.getUuid().equals(uuid)) return entry;
            }
        }
        return null;
    }

    public int getQueueSize() {
        return QueueService.get().getAllQueues().values().stream()
                .mapToInt(Queue::size)
                .sum();
    }

    public Map<Kit, Queue<QueueEntry>> getAllQueues() {
        return kitQueues;
    }

    public Map<IKit, Queue<IQueueEntry>> getQueues() {
        return kitQueues.entrySet().stream().collect(
                HashMap::new,
                (map, entry) -> map.put(
                        entry.getKey(),
                        entry.getValue().stream().map(
                                e -> (IQueueEntry) e).collect(Collectors.toCollection(LinkedList::new)
                        )
                ),
                HashMap::putAll);
    }
}
