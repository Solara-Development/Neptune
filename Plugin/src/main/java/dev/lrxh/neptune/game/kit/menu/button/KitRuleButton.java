package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.impl.KitRule;
import dev.lrxh.neptune.game.kit.menu.KitRulesMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitRuleButton extends Button {
    private final KitRule kitRule;
    private final Kit kit;

    public KitRuleButton(int slot, Kit kit, KitRule kitRule) {
        super(slot, false);
        this.kitRule = kitRule;
        this.kit = kit;
    }

    public static ItemStack buildRuleItem(KitRule rule, boolean enabled) {
        return new ItemBuilder(MenusLocale.valueOf("KIT_RULE_" + rule.name() + "_MATERIAL").getString())
                .name(MenusLocale.valueOf("KIT_RULE_" + rule.name() + (enabled ? "_ENABLED_NAME" : "_DISABLED_NAME")).getString())
                .lore(MenusLocale.valueOf("KIT_RULE_" + rule.name() + "_LORE").getStringList())
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        kit.toggle(kitRule);
        KitService.get().save();
        new KitRulesMenu(kit).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return buildRuleItem(kitRule, kit.is(kitRule));
    }
}
