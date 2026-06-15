package dev.lrxh.neptune.feature.rankedloadout.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.menu.editor.KitEditorMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class RankedLoadoutSelectButton extends Button {
    private final Kit kit;

    public RankedLoadoutSelectButton(int slot, Kit kit) {
        super(slot);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new RankedLoadoutListMenu(kit).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(kit.getIcon())
                .name(MenusLocale.KIT_EDITOR_SELECT_KIT_NAME.getString().replace("<kit>", kit.getDisplayName()))
                .componentLore(ItemUtils.getLore(MenusLocale.KIT_EDITOR_SELECT_LORE.getStringList(),
                        Placeholder.parsed("kit", kit.getDisplayName())), player)
                .build();
    }
}
