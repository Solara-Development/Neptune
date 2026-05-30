package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.feature.hotbar.HotbarService;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.menu.KitsManagementMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.PlayerUtil;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.sign.SignInputMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitCreateButton extends Button {

    public KitCreateButton(int slot) {
        super(slot, false);
    }

    @Override
    public void onClick(ClickType type, Player player) {
        player.closeInventory();
        SignInputMenu.open(player, "", "Enter kit name", input -> {
            Kit kit = new Kit(input, player);
            if (KitService.get().add(kit)) {
                player.sendMessage(CC.error("Kit already exists"));
                return;
            }
            if (input.contains(" ")) {
                player.sendMessage(CC.error("Kit name cannot contain spaces"));
                return;
            }
            player.sendMessage(CC.success("Created kit"));
            PlayerUtil.reset(player);
            HotbarService.get().giveItems(player);
            new KitsManagementMenu().open(player);
            KitService.get().save();
        });
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name("&aCreate Kit").build();
    }
}
