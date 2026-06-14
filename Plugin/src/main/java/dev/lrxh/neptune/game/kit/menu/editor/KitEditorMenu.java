package dev.lrxh.neptune.game.kit.menu.editor;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.kit.menu.editor.button.KitEditorSelectButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

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
        return buttons;
    }
}