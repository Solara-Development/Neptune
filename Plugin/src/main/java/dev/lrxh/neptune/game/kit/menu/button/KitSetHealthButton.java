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

public class KitSetHealthButton extends Button {
    private final Kit kit;

    public KitSetHealthButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        player.closeInventory();
        SignInputMenu.open(player, "", "Enter max health (1-40)", input -> {
            try {
                double health = Double.parseDouble(input);
                if (health < 1 || health > 40) {
                    player.sendMessage(CC.error("Health must be between 1 and 40."));
                    return;
                }
                kit.setHealth(health);
                player.sendMessage(CC.success("Set max health"));
                new KitManagementMenu(kit).open(player);
                KitService.get().save();
            } catch (NumberFormatException e) {
                player.sendMessage(CC.error("Invalid number."));
            }
        });
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.GOLDEN_APPLE).name("&cSet Max Health &7(" + kit.getHealth() + ")").build();
    }
}
