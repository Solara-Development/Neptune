package dev.lrxh.neptune.game.kit.menu;

import dev.lrxh.neptune.game.kit.Kit;
import dev.lrxh.neptune.game.kit.menu.button.*;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.DisplayButton;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class KitManagementMenu extends Menu {
    private final Kit kit;

    public KitManagementMenu(Kit kit) {
        super("&eManage Kit", 45, Filter.FILL);
        this.kit = kit;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new KitSetInvButton(0, kit));
        buttons.add(new KitSetIconButton(1, kit));

        buttons.add(new KitSetHealthButton(4, kit));

        buttons.add(new KitSetKitEditorSlotButton(7, kit));
        buttons.add(new KitSetDamageMultiplierButton(8, kit));

        buttons.add(new KitRulesButton(9, kit));
        buttons.add(new KitSetSlotButton(10, kit));

        buttons.add(new KitDeleteButton(21, kit));
        buttons.add(new DisplayButton(22, kit.getIcon(), " "));
        buttons.add(new KitRenameButton(23, kit));

        buttons.add(new KitSetLeaderboardSlotButton(31, kit));

        buttons.add(new DisplayButton(getSize() - 5, Material.EMERALD, "&aManage Arenas", List.of("&7Click to manage which arenas this kit can be played on"), o -> new KitArenaManagementMenu(kit).open(player)));

        buttons.add(new ReturnButton(getSize() - 9, new KitsManagementMenu()));

        return buttons;
    }
}
