package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.menu.KitManagementMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashSet;
import java.util.Optional;

public class KitSelectLeaderboardSlotButton extends Button {
    private final Kit kit;
    private final LinkedHashSet<Kit> kits = KitService.get().getKits();

    public KitSelectLeaderboardSlotButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        Optional<Kit> existingKit = kits.stream().filter((Kit kit) -> kit.getLeaderboardSlot() == getSlot()).findFirst();
        if (existingKit.isPresent()) {
            player.sendMessage(CC.error("Select an unoccupied slot!"));
            return;
        }
        kit.setLeaderboardSlot(getSlot());
        new KitManagementMenu(kit).open(player);
        player.sendMessage(CC.success("New leaderboard slot set"));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        Optional<Kit> existingKit = kits.stream().filter((Kit kit) -> kit.getLeaderboardSlot() == getSlot()).findFirst();
        if (existingKit.isPresent()) {
            ItemStack icon = existingKit.get().getIcon();
            ItemMeta meta = icon.getItemMeta();
            meta.displayName(CC.color("&eSlot &6" + getSlot() + " &7(&f" + existingKit.get().getDisplayName() + "&7)"));
            icon.setItemMeta(meta);
            return new ItemBuilder(icon).build();
        }
        return new ItemBuilder(Material.getMaterial(MenusLocale.FILTER_MATERIAL.getString())).name("&eSlot &6" + this.getSlot()).build();
    }
}
