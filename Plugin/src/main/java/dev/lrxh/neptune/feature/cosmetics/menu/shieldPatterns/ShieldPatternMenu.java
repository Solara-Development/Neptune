package dev.lrxh.neptune.feature.cosmetics.menu.shieldPatterns;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.cosmetics.CosmeticService;
import dev.lrxh.neptune.feature.cosmetics.impl.cosmetics.shieldpatterns.ShieldPatternPackage;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ShieldPatternMenu extends Menu {
    public ShieldPatternMenu() {
        super(MenusLocale.SHIELD_PATTERNS_TITLE.getString(), MenusLocale.SHIELD_PATTERNS_SIZE.getInt(), Filter.valueOf(MenusLocale.SHIELD_PATTERNS_FILTER.getString()), true);
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (ShieldPatternPackage shieldPatternPackage : CosmeticService.get().getShieldPatternPackages().values()) {
            buttons.add(new ShieldPatternButton(shieldPatternPackage.getSlot(), shieldPatternPackage));
        }

        return buttons;
    }
}
