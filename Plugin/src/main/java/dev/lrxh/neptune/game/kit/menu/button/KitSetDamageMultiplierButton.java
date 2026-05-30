package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.menu.KitManagementMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.sign.SignInputMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitSetDamageMultiplierButton extends Button {
    private final Kit kit;

    public KitSetDamageMultiplierButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        player.closeInventory();
        SignInputMenu.open(player, "", "Enter damage multiplier", input -> {
            try {
                kit.setDamageMultiplier(Double.parseDouble(input));
                player.sendMessage(CC.success("Set damage multiplier"));
                new KitManagementMenu(kit).open(player);
                KitService.get().save();
            } catch (NumberFormatException e) {
                player.sendMessage(CC.error("Invalid number."));
            }
        });
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.BLAZE_POWDER).name("&9Change Damage Multiplier &7(x" + kit.getDamageMultiplier() + ")").build();
    }
}
