package dev.lrxh.neptune.game.arena.menu;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaDuplicator;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.procedure.ArenaProcedureType;
import dev.lrxh.neptune.API;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.DisplayButton;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArenaDuplicatesMenu extends Menu {
    private final Arena arena;

    public ArenaDuplicatesMenu(Arena arena) {
        super("&eManage Duplicates", 45, Filter.FILL);
        this.arena = arena;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 0;
        for (Arena duplicate : ArenaService.get().getDuplicates(arena)) {
            buttons.add(new Button(i++) {
                @Override
                public ItemStack getItemStack(Player player) {
                    return new ItemBuilder(Material.DIAMOND_SWORD)
                            .name("&f" + duplicate.getName())
                            .lore("&7In use: " + (duplicate.isInUse() ? "&aYes" : "&cNo"),
                                    "&7Ready: " + (duplicate.isDoneLoading() ? "&aYes" : "&eLoading..."),
                                    "",
                                    "&eLeft-Click &7to teleport",
                                    "&cRight-Click &7to remove")
                            .build();
                }

                @Override
                public void onClick(ClickType type, Player player) {
                    if (type.isRightClick()) {
                        ArenaService.get().removeDuplicate(duplicate);
                        player.sendMessage(CC.success("Removed duplicate " + duplicate.getName()));
                        new ArenaDuplicatesMenu(arena).open(player);
                    } else if (duplicate.getRedSpawn() != null) {
                        player.teleport(duplicate.getRedSpawn());
                    }
                }
            });
        }

        buttons.add(new DisplayButton(getSize() - 4, Material.LIME_DYE, "&aAdd Duplicate", p -> {
            Profile profile = API.getProfile(p);
            profile.getArenaProcedure().setType(ArenaProcedureType.ADD_DUPLICATE);
            profile.getArenaProcedure().setArena(arena);
            p.closeInventory();
            p.sendMessage(CC.info("Type the amount of duplicates to create &7(max 36 per arena)&f, or type &cCancel"));
        }));

        buttons.add(new DisplayButton(getSize() - 6, Material.EMERALD, "&aRecopy Duplicates", p -> {
            if (!ArenaDuplicator.isAvailable()) {
                p.sendMessage(CC.error("FastAsyncWorldEdit is not installed."));
                return;
            }
            ArenaService.get().recopyDuplicates(arena);
            p.sendMessage(CC.success("Repasting all duplicates of " + arena.getName()));
        }));

        buttons.add(new ReturnButton(getSize() - 9, new ArenaManagementMenu(arena)));
        return buttons;
    }
}
