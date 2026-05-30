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

public class KitSetHealthButton extends Button {
    private final Kit kit;

    public KitSetHealthButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getKitProcedure().setType(KitProcedureType.SET_HEALTH);
        profile.getKitProcedure().setKit(kit);
        player.closeInventory();
        player.sendMessage(CC.info("Send a message containing the max health for this kit (1-40)"));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.GOLDEN_APPLE).name("&cSet Max Health &7(" + kit.getHealth() + ")").build();
    }
}
