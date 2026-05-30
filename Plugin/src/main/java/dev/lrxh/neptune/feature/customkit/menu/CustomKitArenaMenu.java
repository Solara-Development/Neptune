package dev.lrxh.neptune.feature.customkit.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CustomKitArenaMenu extends PaginatedMenu {
    private final CustomKit kit;

    public CustomKitArenaMenu(CustomKit kit) {
        super(MenusLocale.CUSTOM_KIT_ARENAS_TITLE.getString(), MenusLocale.CUSTOM_KIT_ARENAS_SIZE.getInt(), Filter.NONE);
        this.kit = kit;
    }

    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = 0;
        for (Arena arena : ArenaService.get().arenas) {
            if (!arena.isEnabled() || !arena.isSetup()) continue;
            buttons.add(new Button(i++) {
                @Override
                public ItemStack getItemStack(Player p) {
                    boolean added = kit.getArenaNames().contains(arena.getName());
                    return new ItemBuilder(Material.MAP)
                            .name(MenusLocale.CUSTOM_KIT_ARENAS_ITEM_NAME.getString().replace("<arena>", arena.getName()))
                            .lore(added ? MenusLocale.CUSTOM_KIT_ARENAS_SELECTED_LORE.getStringList()
                                    : MenusLocale.CUSTOM_KIT_ARENAS_UNSELECTED_LORE.getStringList()).build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    kit.toggleArena(arena.getName());
                    Profile profile = API.getProfile(p);
                    if (profile != null) Profile.save(profile);
                    open(p);
                }
            });
        }
        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new ReturnButton(getSize() - 9, new CustomKitManageMenu(kit)));
        return buttons;
    }

    @Override
    public int getMaxItemsPerPage() {
        return 36;
    }
}
