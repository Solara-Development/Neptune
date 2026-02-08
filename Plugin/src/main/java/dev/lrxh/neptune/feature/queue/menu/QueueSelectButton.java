package dev.lrxh.neptune.feature.queue.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.leaderboard.LeaderboardService;
import dev.lrxh.neptune.feature.leaderboard.impl.LeaderboardType;
import dev.lrxh.neptune.feature.leaderboard.impl.PlayerEntry;
import dev.lrxh.neptune.feature.queue.QueueEntry;
import dev.lrxh.neptune.feature.queue.QueueService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class QueueSelectButton extends Button {
    private final Kit kit;

    public QueueSelectButton(int slot, Kit kit) {
        super(slot);
        this.kit = kit;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        List<String> lore = new ArrayList<>();

        MenusLocale.QUEUE_SELECT_LORE.getStringList().forEach(line -> {
            String[] split = line.split("_");

            if (split.length != 3) {
                lore.add(line);
                return;
            }
            LeaderboardType leaderboardType = LeaderboardType.value(split[1]);
            if (leaderboardType == null) {
                lore.add(line);
                return;
            }

            List<PlayerEntry> leaderboard = LeaderboardService.get().getPlayerEntries(kit, leaderboardType);

            int i = Integer.parseInt(split[2]);
            PlayerEntry playerEntry = null;

            if (i <= leaderboard.size()) {
                playerEntry = LeaderboardService.get().getLeaderboardSlot(kit, leaderboardType, i);
            }

            if (playerEntry == null) {
                line = line.replaceAll("<player_" + split[1] + "_" + i + ">", "???");
                line = line.replaceAll("<value" + split[1] + "_" + i + ">", "???");
            } else {
                line = line.replaceAll("<player_" + split[1] + "_" + i + ">", playerEntry.getUsername());
                line = line.replaceAll("<value" + split[1] + "_" + i + ">", String.valueOf(playerEntry.getValue()));
            }

            lore.add(line);
        });


        return new ItemBuilder(kit.getIcon()).name(MenusLocale.QUEUE_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(lore, TagResolver.resolver(
                        Placeholder.parsed("kit", kit.getDisplayName()),
                        Placeholder.unparsed("kit-name", kit.getName()),
                        Placeholder.unparsed("playing", String.valueOf(kit.getPlaying())),
                        Placeholder.unparsed("queue", String.valueOf(kit.getQueue()))), player)
                .amount(kit.getQueue())
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        QueueService.get().add(new QueueEntry(kit, player.getUniqueId()), true);
        player.closeInventory();
    }
}