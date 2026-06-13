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
    private final Map<UUID, List<QueueEntry>> playerQueues = new HashMap<>();

    public static QueueService get() {
        if (instance == null) instance = new QueueService();
        return instance;
    }

    public void add(QueueEntry queueEntry, boolean add) {
        UUID playerUUID = queueEntry.getUuid();
        Kit kit = queueEntry.getKit();

        Profile profile = API.getProfile(playerUUID);
        if (!profile.hasState(ProfileState.IN_LOBBY, ProfileState.IN_QUEUE)) return;
        if (profile.getGameData().getParty() != null) return;
        if (queueEntry.getKit().is(KitRule.HIDDEN)) return;

        // Check if player is already in queue for this specific kit
        List<QueueEntry> playerEntries = playerQueues.get(playerUUID);
        if (playerEntries != null) {
            for (QueueEntry entry : playerEntries) {
                if (entry.getKit().equals(kit)) {
                    // Player already in queue for this kit
                    return;
                }
            }
        }

        // Add to kit queue
        kitQueues.computeIfAbsent(kit, k -> new ConcurrentLinkedQueue<>()).offer(queueEntry);
        
        // Add to player queue
        playerQueues.computeIfAbsent(playerUUID, k -> new ArrayList<>()).add(queueEntry);

        if (!profile.hasState(ProfileState.IN_QUEUE)) profile.setState(ProfileState.IN_QUEUE);
        kit.addQueue();

        if (add) {
            QueueJoinEvent event = new QueueJoinEvent(queueEntry);
            Bukkit.getScheduler().runTask(Neptune.get(), () -> Bukkit.getPluginManager().callEvent(event));
            if (event.isCancelled()) return;
            
            // Only send message if this is the player's first queue entry
            List<QueueEntry> currentEntries = playerQueues.get(playerUUID);
            boolean isFirstQueue = (currentEntries != null && currentEntries.size() == 1);
            
            if (isFirstQueue) {
                MessagesLocale.QUEUE_JOIN.send(playerUUID, TagResolver.resolver(
                        Placeholder.parsed("kit", kit.getDisplayName()),
                        Placeholder.unparsed("max-ping", String.valueOf(profile.getSettingData().getMaxPing()))));
            }
        }
    }

    public QueueEntry remove(UUID playerUUID) {
        List<QueueEntry> entries = playerQueues.get(playerUUID);
        if (entries == null || entries.isEmpty()) return null;

        // Remove from all queues
        for (QueueEntry entry : new ArrayList<>(entries)) {
            Kit kit = entry.getKit();
            Queue<QueueEntry> queue = kitQueues.get(kit);
            if (queue != null) {
                queue.remove(entry);
                kit.removeQueue();
            }
        }

        // Clear player's queue list
        playerQueues.remove(playerUUID);

        // Return first entry for compatibility
        return entries.get(0);
    }

    public void remove(QueueEntry queueEntry) {
        remove(queueEntry.getUuid());
    }

    public QueueEntry poll(Kit kit) {
        Queue<QueueEntry> queue = kitQueues.get(kit);
        if (queue == null || queue.isEmpty()) return null;

        List<QueueEntry> entries = new ArrayList<>(queue);
        QueueEntry selectedEntry = entries.get(new Random().nextInt(entries.size()));
        
        // Remove player from ALL queues they're in (not just this one)
        UUID playerUUID = selectedEntry.getUuid();
        remove(playerUUID);
        
        return selectedEntry;
    }

    public QueueEntry get(UUID uuid) {
        List<QueueEntry> entries = playerQueues.get(uuid);
        if (entries == null || entries.isEmpty()) return null;
        return entries.get(0);
    }

    @Override
    public List<IQueueEntry> getAll(UUID uuid) {
        List<QueueEntry> entries = playerQueues.get(uuid);
        return entries != null ? new ArrayList<>(entries) : new ArrayList<>();
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
