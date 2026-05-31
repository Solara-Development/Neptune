package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.menu.KitRulesMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitRoundsButton extends Button {
    private final Kit kit;

    public KitRoundsButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    public static ItemStack buildRoundsItem(int rounds) {
        return new ItemBuilder(MenusLocale.KIT_RULE_ROUNDS_MATERIAL.getString())
                .name(MenusLocale.KIT_RULE_ROUNDS_NAME.getString().replace("<rounds>", String.valueOf(rounds)))
                .lore(MenusLocale.KIT_RULE_ROUNDS_LORE.getStringList().stream()
                        .map(line -> line.replace("<rounds>", String.valueOf(rounds))).toList())
                .amount(rounds)
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        kit.setRounds(Kit.clampRounds(kit.getRounds() + (type.isRightClick() ? -1 : 1)));
        KitService.get().save();
        new KitRulesMenu(kit).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return buildRoundsItem(kit.getRounds());
    }
}
