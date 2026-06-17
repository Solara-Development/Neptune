package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.menu.KitSelectKitEditorSlotMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitSetKitEditorSlotButton extends Button {
    private final Kit kit;

    public KitSetKitEditorSlotButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new KitSelectKitEditorSlotMenu(kit).open(player);
        player.sendMessage(CC.info("Please select a new kit editor slot"));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.LADDER)
                .name("&9Change kit-editor slot &7(" + kit.getKitEditorSlot() + ")")
                .lore("&7Click to change this kit's position in the kit editor")
                .build();
    }
}
