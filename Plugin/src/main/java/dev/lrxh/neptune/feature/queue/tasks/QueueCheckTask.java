package dev.lrxh.neptune.feature.queue.tasks;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.match.MatchService;
import dev.lrxh.neptune.game.match.impl.participant.Participant;
import dev.lrxh.neptune.profile.data.ProfileState;
import dev.lrxh.neptune.profile.data.SettingData;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.tasks.NeptuneRunnable;
import dev.lrxh.neptune.utils.tasks.TaskScheduler;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public class QueueCheckTask extends NeptuneRunnable {
    @Override
    public void run() {

        for (Queue<QueueEntry> queue : QueueService.get().getAllQueues().values()) {
            for (QueueEntry entry : queue) {
                Profile profile = API.getProfile(entry.getUuid());
                if (profile != null && profile.getPlayer() != null) {
                    Player player = profile.getPlayer();
                    player.sendActionBar(CC.returnMessage(player, MessagesLocale.QUEUE_ACTION_BAR.getString()));
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

            Profile profile1 = API.getProfile(uuid1);
            Profile profile2 = API.getProfile(uuid2);

            profile1.setState(ProfileState.IN_LOBBY);
            profile2.setState(ProfileState.IN_LOBBY);

            if (!queueEntry1.getKit().equals(queueEntry2.getKit())) {
                QueueService.get().add(queueEntry1, false);
                QueueService.get().add(queueEntry2, false);
                continue;
            }

            SettingData settings1 = profile1.getSettingData();
            SettingData settings2 = profile2.getSettingData();

            int ping1 = PlayerUtil.getPing(uuid1);
            int ping2 = PlayerUtil.getPing(uuid2);

            if (!(ping2 <= settings1.getMaxPing() && ping1 <= settings2.getMaxPing())) {
                QueueService.get().add(queueEntry1, false);
                QueueService.get().add(queueEntry2, false);
                continue;
            }

            Player player1 = profile1.getPlayer();
            Player player2 = profile2.getPlayer();
            if (player1 == null || player2 == null) {
                continue;
            }

            kit.getRandomArena().thenAccept(arena -> {
                if (arena == null) {
                    PlayerUtil.sendMessage(uuid1, CC.error("No valid arena was found for this kit!"));
                    PlayerUtil.sendMessage(uuid2, CC.error("No valid arena was found for this kit!"));
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
                                kit.is(KitRule.BEST_OF_THREE) ? 3 : 1);
                    }
                });
            });
        }
    }

}
