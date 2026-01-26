package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.data.KitData;
import dev.lrxh.neptune.providers.clickable.Replacement;
import dev.lrxh.neptune.providers.placeholder.PlaceholderUtil;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import me.clip.placeholderapi.PlaceholderAPI;

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
        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.STAT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .lore(PlaceholderUtil.format(MenusLocale.STAT_LORE.getStringList(), player))
                .build();
    }
}