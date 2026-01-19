package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
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
        KitData data = API.getProfile(target).getGameData().get(kit);

        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.STAT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(ItemUtils.getLore(MenusLocale.STAT_LORE.getStringList(), new Replacement("<kit>", kit.getDisplayName()),
                        new Replacement("<wins>", String.valueOf(data.getWins())),
                        new Replacement("<elo>", String.valueOf(data.getElo())),
                        new Replacement("<losses>", String.valueOf(data.getLosses())),
                        new Replacement("<kills>", String.valueOf(data.getKills())),
                        new Replacement("<deaths>", String.valueOf(data.getDeaths())),
                        new Replacement("<win_streak_current>", String.valueOf(data.getCurrentStreak())),
                        new Replacement("<win_streak_best>", String.valueOf(data.getBestStreak())),
                        new Replacement("<division>", String.valueOf(data.getDivision().getDisplayName())),
                        new Replacement("<played>", String.valueOf(data.getWins() + data.getLosses())),
                        new Replacement("<kill_death_ratio>", String.valueOf(data.getKdr()))), player)

                .build();
    }
}