package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.itembrowser.ItemBrowserService;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.menu.WhitelistedBlocksMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class RemoveWhitelistBlockButton extends Button {
    private final Arena arena;

    public RemoveWhitelistBlockButton(int slot, Arena arena) {
        super(slot, false);
        this.arena = arena;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        if (arena.getWhitelistedBlocks().isEmpty()) {
            player.sendMessage(CC.error(MenusLocale.ARENA_WHITELIST_EMPTY.getString()));
            return;
        }

        player.closeInventory();
        String section = "arena-whitelist-remove-" + arena.getName();
        ItemBrowserService.get().registerSection(section,
                arena.getWhitelistedBlocks().stream().map(Material::name).toList());

        ItemBrowserService.get().openBrowser(player, section, material -> {
            if (arena.getWhitelistedBlocks().remove(material)) {
                player.sendMessage(CC.success("Removed " + material.name() + " from whitelist."));
            } else {
                player.sendMessage(CC.error(material.name() + " is not whitelisted."));
            }
            ArenaService.get().save();
            new WhitelistedBlocksMenu(arena).open(player);
        }, () -> new WhitelistedBlocksMenu(arena).open(player));
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(MenusLocale.ARENA_WHITELIST_REMOVE_MATERIAL.getString())
                .name(MenusLocale.ARENA_WHITELIST_REMOVE_NAME.getString())
                .lore(MenusLocale.ARENA_WHITELIST_REMOVE_LORE.getStringList())
                .build();
    }
}
