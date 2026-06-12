package dev.lrxh.neptune.feature.queue.tasks;

import dev.lrxh.api.queue.IQueueEntry;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import dev.lrxh.neptune.utils.tasks.TaskScheduler;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class QueueCheckTask extends NeptuneRunnable {
    @Override
    public void run() {
        // Track players already matched in this tick to prevent double-matching
        Set<UUID> matchedPlayers = new HashSet<>();

        for (Queue<QueueEntry> queue : QueueService.get().getAllQueues().values()) {
            for (QueueEntry entry : queue) {
                Profile profile = API.getProfile(entry.getUuid());
                if (profile != null && profile.getPlayer() != null) {
                    Player player = profile.getPlayer();
                    List<IQueueEntry> playerQueues = QueueService.get().getAll(entry.getUuid());
                    int queueCount = playerQueues.size();
                    
                    // Calculate total time in queue (use the longest queue time)
                    long maxTime = 0;
                    for (IQueueEntry queueEntry : playerQueues) {
                        long elapsed = queueEntry.getTime().getElapsed();
                        if (elapsed > maxTime) {
                            maxTime = elapsed;
                        }
                    }
                    
                    String timeFormatted = String.format("%d:%02d", maxTime / 60000, (maxTime / 1000) % 60);
                    
                    // Show kit name if in 1 queue, show count if in multiple queues
                    if (queueCount == 1) {
                        player.sendActionBar(CC.returnMessage(player, MessagesLocale.QUEUE_ACTION_BAR_SINGLE.getString(),
                                TagResolver.resolver(
                                        Placeholder.parsed("kit", entry.getKit().getDisplayName()),
                                        Placeholder.unparsed("time", timeFormatted)
                                )));
                    } else {
                        player.sendActionBar(CC.returnMessage(player, MessagesLocale.QUEUE_ACTION_BAR.getString(),
                                TagResolver.resolver(
                                        Placeholder.unparsed("queue-count", String.valueOf(queueCount)),
                                        Placeholder.unparsed("time", timeFormatted)
                                )));
                    }
                }
            }
        }

        for (Map.Entry<Kit, Queue<QueueEntry>> entry : QueueService.get().getAllQueues().entrySet()) {
            Kit kit = entry.getKey();
            Queue<QueueEntry> kitQueue = entry.getValue();


            if (kitQueue.size() < 2) {
                continue;
            }

            QueueEntry queueEntry1 = QueueService.get().poll(kit);
            QueueEntry queueEntry2 = QueueService.get().poll(kit);

            UUID uuid1 = queueEntry1.getUuid();
            UUID uuid2 = queueEntry2.getUuid();

            // Check if either player was already matched in this tick
            if (matchedPlayers.contains(uuid1) || matchedPlayers.contains(uuid2)) {
                QueueService.get().add(queueEntry1, false);
                QueueService.get().add(queueEntry2, false);
                continue;
            }

            Profile profile1 = API.getProfile(uuid1);
            Profile profile2 = API.getProfile(uuid2);

            if (!queueEntry1.getKit().equals(queueEntry2.getKit())) {
                QueueService.get().add(queueEntry1, false);
                QueueService.get().add(queueEntry2, false);
                continue;
            }

            SettingData settings1 = profile1.getSettingData();
            SettingData settings2 = profile2.getSettingData();

            int ping1 = PlayerUtil.getPing(uuid1);
            int ping2 = PlayerUtil.getPing(uuid2);

            boolean pingInRange = ping2 <= settings1.getMaxPing() && ping1 <= settings2.getMaxPing();
            // bypass ping range once a player has waited 10s
            boolean waitedTooLong = queueEntry1.getTime().getElapsed() >= 10_000
                    || queueEntry2.getTime().getElapsed() >= 10_000;

            if (!pingInRange && !waitedTooLong) {
                QueueService.get().add(queueEntry1, false);
                QueueService.get().add(queueEntry2, false);
                continue;
            }

            Player player1 = profile1.getPlayer();
            Player player2 = profile2.getPlayer();
            if (player1 == null || player2 == null) {
                continue;
            }

            // Mark both players as matched in this tick
            matchedPlayers.add(uuid1);
            matchedPlayers.add(uuid2);

            kit.getRandomArena().thenAccept(arena -> {
                if (arena == null) {
                    QueueService.get().add(queueEntry1, false);
                    QueueService.get().add(queueEntry2, false);
                    matchedPlayers.remove(uuid1);
                    matchedPlayers.remove(uuid2);
                    return;
                }

                Participant participant1 = new Participant(player1);
                Participant participant2 = new Participant(player2);

                MessagesLocale.MATCH_FOUND.send(uuid1, TagResolver.resolver(
                        Placeholder.parsed("kit", kit.getDisplayName()),
                        Placeholder.parsed("arena", arena.getDisplayName()),
                        Placeholder.unparsed("opponent", participant2.getNameUnColored()),
                        Placeholder.unparsed("opponent-ping", String.valueOf(ping2)),
                        Placeholder.unparsed("opponent-elo", String.valueOf(profile2.getGameData().get(kit).getElo())),
                        Placeholder.unparsed("elo", String.valueOf(profile1.getGameData().get(kit).getElo())),
                        Placeholder.unparsed("ping", String.valueOf(ping1))));

                MessagesLocale.MATCH_FOUND.send(uuid2, TagResolver.resolver(
                        Placeholder.parsed("kit", kit.getDisplayName()),
                        Placeholder.parsed("arena", arena.getDisplayName()),
                        Placeholder.unparsed("opponent", participant1.getNameUnColored()),
                        Placeholder.unparsed("opponent-ping", String.valueOf(ping1)),
                        Placeholder.unparsed("opponent-elo", String.valueOf(profile1.getGameData().get(kit).getElo())),
                        Placeholder.unparsed("elo", String.valueOf(profile2.getGameData().get(kit).getElo())),
                        Placeholder.unparsed("ping", String.valueOf(ping2))));

                TaskScheduler.get().startTaskCurrentTick(new NeptuneRunnable() {
                    @Override
                    public void run() {
                        MatchService.get().startMatch(participant1, participant2, kit, arena, false,
                                kit.getRounds());
                    }
                });
            });
        }
    }

}
