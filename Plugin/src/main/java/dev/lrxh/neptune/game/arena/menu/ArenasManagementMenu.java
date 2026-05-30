package dev.lrxh.neptune.game.arena.menu;

import dev.lrxh.neptune.Neptune;
import dev.lrxh.neptune.game.arena.Arena;
import dev.lrxh.neptune.game.arena.ArenaService;
import dev.lrxh.neptune.game.arena.menu.button.ArenaCreateButton;
import dev.lrxh.neptune.game.arena.menu.button.ArenaSelectButton;
import dev.lrxh.neptune.main.MainMenu;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import dev.lrxh.neptune.utils.menu.impl.DisplayButton;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenasManagementMenu extends PaginatedMenu {
    public ArenasManagementMenu() {
        super("&eArena Management", 54, Filter.FILL);
    }

    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int i = 0;
        for (Arena arena : ArenaService.get().getArenas()) {
            buttons.add(new ArenaSelectButton(i++, arena));
        }

        buttons.add(new ArenaCreateButton(getSize() - 5));
        buttons.add(new ReturnButton(getSize() - 9, new MainMenu()));

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new ArenaCreateButton(getSize() - 5));
        buttons.add(new ReturnButton(getSize() - 9, new MainMenu()));
        if (Neptune.get().isDuplicatesEnabled()) {
            buttons.add(new DisplayButton(getSize() - 1, Material.EMERALD_BLOCK, "&aRecopy All Duplicates", p -> {
                ArenaService.get().recopyAll();
                p.sendMessage(CC.success("Repasting all duplicates of every arena."));
            }));
        }

        return buttons;
    }
}
