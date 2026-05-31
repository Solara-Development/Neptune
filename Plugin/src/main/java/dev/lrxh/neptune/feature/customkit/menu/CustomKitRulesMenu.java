package dev.lrxh.neptune.feature.customkit.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.kit.menu.button.KitRoundsButton;
import dev.lrxh.neptune.game.kit.menu.button.KitRuleButton;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomKitRulesMenu extends Menu {
    private final CustomKit kit;

    public CustomKitRulesMenu(CustomKit kit) {
        super(MenusLocale.CUSTOM_KIT_RULES_TITLE.getString(), MenusLocale.CUSTOM_KIT_RULES_SIZE.getInt(), Filter.FILL);
        this.kit = kit;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = 0;
        for (KitRule rule : KitRule.values()) {
            if (!rule.isCustomKit()) continue;
            buttons.add(new Button(i++) {
                @Override
                public ItemStack getItemStack(Player p) {
                    return KitRuleButton.buildRuleItem(rule, kit.is(rule));
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    kit.toggle(rule);
                    Profile profile = API.getProfile(p);
                    if (profile != null) Profile.save(profile);
                    CustomKitRulesMenu.this.update(p);
                }
            });
        }
        buttons.add(new Button(getSize() - 5) {
            @Override
            public ItemStack getItemStack(Player p) {
                return KitRoundsButton.buildRoundsItem(kit.getRounds());
            }

            @Override
            public void onClick(ClickType type, Player p) {
                kit.setRounds(Kit.clampRounds(kit.getRounds() + (type.isRightClick() ? -1 : 1)));
                Profile profile = API.getProfile(p);
                if (profile != null) Profile.save(profile);
                CustomKitRulesMenu.this.update(p);
            }
        });
        buttons.add(new ReturnButton(getSize() - 9, new CustomKitManageMenu(kit)));
        return buttons;
    }
}
