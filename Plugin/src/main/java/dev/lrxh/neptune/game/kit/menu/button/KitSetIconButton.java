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

public class KitSetIconButton extends Button {
    private final Kit kit;

    public KitSetIconButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Profile profile = API.getProfile(player);
        profile.getKitProcedure().setType(KitProcedureType.SET_ICON);
        profile.getKitProcedure().setKit(kit);
        player.closeInventory();
        player.sendMessage(CC.info("Hold the the item in your press and type &aDone"));

    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.PAINTING)
                .name("&dSet Icon")
                .lore("&7Hold an item and click to set it as this kit's icon")
                .build();
    }
}
