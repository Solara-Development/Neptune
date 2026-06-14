package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.ArenaWhitelistUtils;
import dev.lrxh.neptune.game.arena.menu.WhitelistedBlocksMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AddAllWhitelistBlocksButton extends Button {
    private final Arena arena;

    public AddAllWhitelistBlocksButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        List<Material> breakableBlocks = ArenaWhitelistUtils.getBreakableBlocks();
        int added = 0;

        for (Material material : breakableBlocks) {
            if (!arena.getWhitelistedBlocks().contains(material)) {
                arena.getWhitelistedBlocks().add(material);
                added++;
            }
        }

        ArenaService.get().save();
        player.sendMessage(CC.success("Added " + added + " blocks to the whitelist "
                + "(" + breakableBlocks.size() + " breakable block types, protected blocks excluded)."));
        new WhitelistedBlocksMenu(arena).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(MenusLocale.ARENA_WHITELIST_ADD_ALL_MATERIAL.getString())
                .name(MenusLocale.ARENA_WHITELIST_ADD_ALL_NAME.getString())
                .lore(MenusLocale.ARENA_WHITELIST_ADD_ALL_LORE.getStringList())
                .build();
    }
}
