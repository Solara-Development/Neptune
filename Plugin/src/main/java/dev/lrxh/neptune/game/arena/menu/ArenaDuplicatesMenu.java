package dev.lrxh.neptune.game.arena.menu;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaDuplicator;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.DisplayButton;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import dev.lrxh.neptune.utils.sign.SignInputMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
            p.closeInventory();
            SignInputMenu.open(p, "", "Amount of duplicates (max 36)", input -> {
                final int amount;
                try {
                    amount = Integer.parseInt(input.trim());
                } catch (NumberFormatException e) {
                    p.sendMessage(CC.error("Invalid number"));
                    return;
                }
                if (amount <= 0) {
                    p.sendMessage(CC.error("Amount must be at least 1"));
                    return;
                }

                int existing = ArenaService.get().getDuplicates(arena).size();
                if (existing >= 36) {
                    p.sendMessage(CC.error("This arena already has the maximum of 36 duplicates"));
                    return;
                }
                int toCreate = Math.min(amount, 36 - existing);
                p.sendMessage(CC.info("Creating " + toCreate + " duplicate(s) of " + arena.getName() + "..."));

                List<CompletableFuture<?>> futures = new ArrayList<>();
                for (int n = 0; n < toCreate; n++) futures.add(ArenaService.get().createDuplicate(arena));

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).whenComplete((v, t) -> {
                    p.sendMessage(t == null
                            ? CC.success("Created " + toCreate + " duplicate(s)")
                            : CC.error("Some duplicates failed to create"));
                    new ArenaDuplicatesMenu(arena).open(p);
                });
            });
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
