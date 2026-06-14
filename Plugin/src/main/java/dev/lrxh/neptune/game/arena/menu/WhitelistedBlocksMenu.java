package dev.lrxh.neptune.game.arena.menu;

import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.game.arena.menu.button.AddAllWhitelistBlocksButton;
import dev.lrxh.neptune.game.arena.menu.button.AddWhitelistBlockButton;
import dev.lrxh.neptune.game.arena.menu.button.RemoveWhitelistBlockButton;
import dev.lrxh.neptune.game.arena.menu.button.WhitelistedBlockButton;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class WhitelistedBlocksMenu extends PaginatedMenu {
    private final Arena arena;

    public WhitelistedBlocksMenu(Arena arena) {
        super(MenusLocale.ARENA_WHITELIST_TITLE.getString(), 54, Filter.NONE);
        this.arena = arena;
    }

    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = 0;
        for (Material material : arena.getWhitelistedBlocks()) {
            buttons.add(new WhitelistedBlockButton(i++, material, arena));
        }
        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new ReturnButton(getSize() - 9, new ArenaManagementMenu(arena)));
        buttons.add(new AddAllWhitelistBlocksButton(getSize() - 7, arena));
        buttons.add(new RemoveWhitelistBlockButton(getSize() - 6, arena));
        buttons.add(new AddWhitelistBlockButton(getSize() - 5, arena));
        return buttons;
    }

    @Override
    public int getMaxItemsPerPage() {
        return 36;
    }
}