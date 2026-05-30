package dev.lrxh.neptune.feature.customkit.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
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
                    return new ItemBuilder(rule.getIcon())
                            .name((kit.is(rule) ? "&a" : "&c") + rule.getName())
                            .lore("&7" + rule.getDescription(), "", "&7Click to toggle").build();
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
        buttons.add(new ReturnButton(getSize() - 9, new CustomKitManageMenu(kit)));
        return buttons;
    }
}
