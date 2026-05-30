package dev.lrxh.neptune.game.kit.menu.button;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.KitService;
import dev.lrxh.neptune.game.kit.menu.KitArenaManagementMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class KitArenaButton extends Button {
    private final Kit kit;
    private final Arena arena;

    public KitArenaButton(int slot, Kit kit, Arena arena) {
        super(slot, false);
        this.kit = kit;
        this.arena = arena;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        kit.toggleArena(arena);
        KitService.get().save();
        new KitArenaManagementMenu(kit).open(player);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        return new ItemBuilder(Material.MAP).name("&f" + arena.getName() + " &7(" + arena.getDisplayName() + "&7)").lore(kit.isArenaAdded(arena) ? "&aArena Added" : "&cArena not added", "&7Press to toggle").build();
    }
}
