package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.menu.KitSelectSlotMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitSetSlotButton extends Button {
    private final Kit kit;

    public KitSetSlotButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new KitSelectSlotMenu(kit).open(player);
        player.sendMessage(CC.info("Please select the new slot"));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.IRON_DOOR)
                .name("&9Change slot &7(" + kit.getSlot() + ")")
                .lore("&7Click to change this kit's menu slot position")
                .build();
    }
}
