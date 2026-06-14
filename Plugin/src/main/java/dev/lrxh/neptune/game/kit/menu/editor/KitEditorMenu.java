package dev.lrxh.neptune.game.kit.menu.editor;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.customkit.menu.CustomKitsMenu;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.kit.menu.editor.button.KitEditorSelectButton;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class KitEditorMenu extends Menu {

    public KitEditorMenu() {
        super(MenusLocale.KIT_EDITOR_SELECT_TITLE.getString(), MenusLocale.KIT_EDITOR_SELECT_SIZE.getInt(), Filter.valueOf(MenusLocale.KIT_EDITOR_SELECT_FILTER.getString()));
    }


    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Kit kit : KitService.get().kits) {
            if (kit.getRules().get(KitRule.HIDDEN)) continue;
            if (kit.is(KitRule.ALLOW_KIT_EDITOR)) {
                buttons.add(new KitEditorSelectButton(kit.getKitEditorSlot(), kit));
            }
        }

        buttons.add(new Button(MenusLocale.KIT_EDITOR_CUSTOM_KITS_SLOT.getInt()) {
            @Override
            public ItemStack getItemStack(Player player) {
                return new ItemBuilder(Material.valueOf(MenusLocale.KIT_EDITOR_CUSTOM_KITS_MATERIAL.getString()))
                        .name(MenusLocale.KIT_EDITOR_CUSTOM_KITS_NAME.getString())
                        .lore(MenusLocale.KIT_EDITOR_CUSTOM_KITS_LORE.getStringList())
                        .build();
            }

            @Override
            public void onClick(ClickType type, Player player) {
                new CustomKitsMenu().open(player);
            }
        });

        return buttons;
    }
}
