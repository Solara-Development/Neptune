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

public class KitRenameButton extends Button {
    private final Kit kit;

    public KitRenameButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        player.closeInventory();
        SignInputMenu.open(player, kit.getDisplayName(), "Enter display name", input -> {
            kit.setDisplayName(input);
            player.sendMessage(CC.success("Renamed kit"));
            new KitManagementMenu(kit).open(player);
            KitService.get().save();
        });
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.NAME_TAG).name("&eRename kit &7(" + kit.getDisplayName() + "&7)").build();
    }
}
