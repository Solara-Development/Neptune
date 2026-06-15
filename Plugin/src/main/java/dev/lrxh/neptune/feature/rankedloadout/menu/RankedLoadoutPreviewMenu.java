package dev.lrxh.neptune.feature.rankedloadout.menu;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.rankedloadout.RankedLoadout;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RankedLoadoutPreviewMenu extends Menu {
    private final Kit kit;
    private final RankedLoadout loadout;

    public RankedLoadoutPreviewMenu(Kit kit, RankedLoadout loadout) {
        super(MenusLocale.RANKED_LOADOUT_PREVIEW_TITLE.getString().replace("<loadout>", loadout.getDisplayName()),
                MenusLocale.CUSTOM_KIT_EDITOR_SIZE.getInt(), Filter.FILL);
        this.kit = kit;
        this.loadout = loadout;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (int es = 0; es <= 35; es++) {
            int ci = es <= 26 ? es + 9 : es - 27;
            buttons.add(previewSlot(es, ci));
        }
        String armor = MenusLocale.CUSTOM_KIT_EDITOR_SLOT_MATERIAL.getString();
        buttons.add(previewSlot(45, 39, armor, MenusLocale.CUSTOM_KIT_EDITOR_HELMET_NAME.getString()));
        buttons.add(previewSlot(46, 38, armor, MenusLocale.CUSTOM_KIT_EDITOR_CHESTPLATE_NAME.getString()));
        buttons.add(previewSlot(47, 37, armor, MenusLocale.CUSTOM_KIT_EDITOR_LEGGINGS_NAME.getString()));
        buttons.add(previewSlot(48, 36, armor, MenusLocale.CUSTOM_KIT_EDITOR_BOOTS_NAME.getString()));
        buttons.add(previewSlot(50, 40, armor, MenusLocale.CUSTOM_KIT_EDITOR_OFFHAND_NAME.getString()));

        buttons.add(new ReturnButton(53, new RankedLoadoutListMenu(kit)));
        return buttons;
    }

    private Button previewSlot(int editorSlot, int contentsIndex) {
        return previewSlot(editorSlot, contentsIndex,
                MenusLocale.CUSTOM_KIT_EDITOR_EMPTY_MATERIAL.getString(),
                MenusLocale.CUSTOM_KIT_EDITOR_EMPTY_NAME.getString());
    }

    private Button previewSlot(int editorSlot, int contentsIndex, String placeholder, String label) {
        return new Button(editorSlot) {
            @Override
            public ItemStack getItemStack(Player p) {
                ItemStack item = loadout.itemAt(contentsIndex);
                if (item != null && !item.getType().isAir()) return item.clone();
                return new ItemBuilder(placeholder).name(label).build();
            }
        };
    }
}
