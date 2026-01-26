package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.providers.placeholder.PlaceholderUtil;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StatButton extends Button {
    private final Kit kit;
    private final Player target;

    public StatButton(int slot, Kit kit, Player target) {
        super(slot);
        this.kit = kit;
        this.target = target;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        KitData kitData = API.getProfile(target).getGameData().get(kit);
        List<String> lore = PlaceholderUtil.format(MenusLocale.STAT_LORE.getStringList()
                .stream()
                .map(
                        line -> line
                                .replaceAll("<kit>", String.valueOf(kit.getDisplayName()))
                                .replaceAll("<division>",
                                        kitData.getDivision() != null
                                                ? String.valueOf(kitData.getDivision().getDisplayName())
                                                : "None")
                                .replaceAll("<wins>", String.valueOf(kitData.getWins()))
                                .replaceAll("<losses>", String.valueOf(kitData.getLosses()))
                                .replaceAll("<elo>", String.valueOf(kitData.getElo()))
                                .replaceAll("<played>", String.valueOf(kitData.getWins() + kitData.getLosses()))
                                .replaceAll("<currentStreak>", String.valueOf(kitData.getCurrentStreak()))
                                .replaceAll("<bestStreak>", String.valueOf(kitData.getBestStreak()))
                                .replaceAll("<kills>", String.valueOf(kitData.getKills()))
                                .replaceAll("<deaths>", String.valueOf(kitData.getDeaths()))
                                .replaceAll("<kdr>", String.valueOf(kitData.getKdr())))
                .toList(), player);
        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.STAT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(lore)
                .build();
    }
}