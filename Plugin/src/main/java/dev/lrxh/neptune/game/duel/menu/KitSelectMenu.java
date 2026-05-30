package dev.lrxh.neptune.game.duel.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitSelectMenu extends Menu {
    private final Player receiver;
    private final boolean party;

    public KitSelectMenu(Player receiver, boolean party) {
        super(MenusLocale.DUEL_TITLE.getString().replaceAll("<target>", receiver.getName()), MenusLocale.DUEL_SIZE.getInt(), Filter.valueOf(MenusLocale.DUEL_FILTER.getString()));
        this.receiver = receiver;
        this.party = party;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Kit kit : KitService.get().kits) {
            if (kit.getRules().get(KitRule.HIDDEN)) continue;
            buttons.add(new KitSelectButton(kit.getSlot(), kit, receiver, party));
        }

        buttons.add(new Button(MenusLocale.DUEL_CUSTOM_KIT_BUTTON_SLOT.getInt()) {
            @Override
            public ItemStack getItemStack(Player p) {
                return new ItemBuilder(MenusLocale.DUEL_CUSTOM_KIT_BUTTON_MATERIAL.getString())
                        .name(MenusLocale.DUEL_CUSTOM_KIT_BUTTON_NAME.getString())
                        .lore(MenusLocale.DUEL_CUSTOM_KIT_BUTTON_LORE.getStringList()).build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                new CustomKitSelectMenu(receiver, party).open(p);
            }
        });

        return buttons;
    }
}
