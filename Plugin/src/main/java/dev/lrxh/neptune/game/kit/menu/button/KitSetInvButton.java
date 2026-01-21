package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.procedure.KitProcedureType;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitSetInvButton extends Button {
    private final Kit kit;

    public KitSetInvButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getKitProcedure().setType(KitProcedureType.SET_INV);
        profile.getKitProcedure().setKit(kit);
        player.closeInventory();
        player.getInventory().setContents(kit.getItems().toArray(new ItemStack[0]));
        player.sendMessage(CC.info("Once you're done changing around, type &aDone"));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.LAVA_BUCKET).name("&dSet Inventory").build();
    }
}
