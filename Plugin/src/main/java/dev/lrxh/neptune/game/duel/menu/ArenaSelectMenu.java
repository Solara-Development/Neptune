package dev.lrxh.neptune.game.duel.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.duel.DuelRequest;
import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ArenaSelectMenu extends Menu {
    private final Kit kit;
    private final UUID receiver;
    private final int round;

    public ArenaSelectMenu(Kit kit, UUID receiver, int round) {
        super(MenusLocale.ARENA_TITLE.getString(), MenusLocale.ARENA_SIZE.getInt(), Filter.valueOf(MenusLocale.ARENA_FILTER.getString()));
        this.kit = kit;
        this.receiver = receiver;
        this.round = round;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = MenusLocale.ARENA_LIST_STARTING_SLOT.getInt();

        buttons.add(new Button(MenusLocale.ARENA_RANDOM_ITEM_SLOT.getInt()) {
            @Override
            public ItemStack getItemStack(Player p) {
                return new ItemBuilder(MenusLocale.ARENA_RANDOM_ITEM_MATERIAL.getString())
                        .name(MenusLocale.ARENA_RANDOM_ITEM_NAME.getString())
                        .lore(MenusLocale.ARENA_RANDOM_ITEM_LORE.getStringList())
                        .build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                kit.getRandomArena().thenAccept(arena -> {
                    Profile profile = API.getProfile(receiver);
                    if (profile == null) return;
                    if (arena == null) {
                        p.sendMessage(CC.error("No arena found, please contact an admin"));
                        return;
                    }
                    DuelRequest duelRequest = new DuelRequest(p.getUniqueId(), kit, arena, false, round);
                    profile.sendDuel(duelRequest);
                    Bukkit.getScheduler().runTask(Neptune.get(), () -> p.closeInventory());
                });
            }
        });
        int rows = MenusLocale.ARENA_SIZE.getInt() / 9;
        boolean isBorder = MenusLocale.ARENA_FILTER.getString().equals("BORDER");
        for (Arena arena : kit.getArenas()) {
            int row = i / 9;
            int col = i % 9;
            if (col == 8 && isBorder) i += 2;
            if (row == rows - 1 && isBorder) i += 9;
            buttons.add(new Button(i++) {
                @Override
                public ItemStack getItemStack(Player p) {
                    return new ItemBuilder(Material.MAP)
                            .name(MenusLocale.ARENA_ITEM_NAME.getString().replace("<arena>", arena.getDisplayName()))
                            .lore(MenusLocale.ARENA_ITEM_LORE.getStringList().stream()
                                    .map(it -> it.replace("<arena>", arena.getDisplayName()))
                                    .collect(Collectors.toList()))
                            .build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    Profile profile = API.getProfile(receiver);
                    if (profile == null) return;
                    player.closeInventory();
                    arena.createDuplicate().thenAccept(duplicate -> {
                        DuelRequest duelRequest = new DuelRequest(p.getUniqueId(), kit, duplicate, false, round);
                        profile.sendDuel(duelRequest);
                    });
                }
            });
        }

        return buttons;
    }
}