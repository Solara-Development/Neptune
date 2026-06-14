package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.itembrowser.ItemBrowserService;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.menu.WhitelistedBlocksMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class AddWhitelistBlockButton extends Button {
    private final Arena arena;

    public AddWhitelistBlockButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        player.closeInventory();
        player.sendMessage(CC.info("Select a block to whitelist!"));

        ItemBrowserService service = ItemBrowserService.get();
        String section = "blocks";
        service.openBrowser(player, section, material -> {
            if (!arena.getWhitelistedBlocks().contains(material)) {
                arena.getWhitelistedBlocks().add(material);
                player.sendMessage(CC.success("Added block " + material.name() + " to whitelist."));
            } else {
                player.sendMessage(CC.error(material.name() + " is already whitelisted."));
            }
            ArenaService.get().save();
            new WhitelistedBlocksMenu(arena).open(player);
        }, () -> new WhitelistedBlocksMenu(arena).open(player));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(MenusLocale.ARENA_WHITELIST_ADD_MATERIAL.getString())
                .name(MenusLocale.ARENA_WHITELIST_ADD_NAME.getString())
                .lore(MenusLocale.ARENA_WHITELIST_ADD_LORE.getStringList())
                .build();
    }
}