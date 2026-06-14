package dev.lrxh.neptune.game.arena.menu.button;

import dev.lrxh.neptune.configs.impl.MenusLocale;
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

public class WhitelistedBlockButton extends Button {
    private final Material material;
    private final Arena arena;

    public WhitelistedBlockButton(int slot, Material material, Arena arena) {
        super(slot);
        this.material = material;
        this.arena = arena;
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(material)
                .name(MenusLocale.ARENA_WHITELIST_ITEM_NAME.getString().replace("<block>", material.name()))
                .lore(MenusLocale.ARENA_WHITELIST_ITEM_REMOVE_LORE.getStringList())
                .build();
    }

    @Override
    public void onClick(ClickType type, Player player) {
        if (!type.equals(ClickType.LEFT))
            return;

        if (arena.getWhitelistedBlocks().remove(material)) {
            player.sendMessage(CC.success("Removed " + material.name() + " from whitelist."));
        } else {
            player.sendMessage(CC.error(material.name() + " is not whitelisted."));
        }

        ArenaService.get().save();
        new WhitelistedBlocksMenu(arena).open(player);
    }
}
