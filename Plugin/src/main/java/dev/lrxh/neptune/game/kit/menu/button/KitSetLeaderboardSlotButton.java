package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.menu.KitSelectLeaderboardSlotMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitSetLeaderboardSlotButton extends Button {
    private final Kit kit;

    public KitSetLeaderboardSlotButton(int slot, Kit kit) {
        super(slot, false);
        this.kit = kit;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        new KitSelectLeaderboardSlotMenu(kit).open(player);
        player.sendMessage(CC.info("Please select the new leaderboard slot"));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.ITEM_FRAME).name("&9Change leaderboard slot &7(" + kit.getLeaderboardSlot() + ")").build();
    }
}
