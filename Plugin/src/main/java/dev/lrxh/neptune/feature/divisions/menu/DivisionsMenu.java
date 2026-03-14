package dev.lrxh.neptune.feature.divisions.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.divisions.DivisionService;
import dev.lrxh.neptune.feature.divisions.impl.Division;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.DisplayButton;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DivisionsMenu extends Menu {

    public DivisionsMenu() {
        super(MenusLocale.DIVISIONS_TITLE.getString(), MenusLocale.DIVISIONS_SIZE.getInt(), Filter.valueOf(MenusLocale.DIVISIONS_FILTER.getString()));
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        ArrayList<Division> divisions = new ArrayList<>(DivisionService.get().divisions);
        Collections.reverse(divisions);

        for (Division division : divisions) {
            buttons.add(new DisplayButton(division.getSlot(), getItemStack(player, division)));
        }
        if (MenusLocale.DIVISIONS_ELO_BUTTON_ENABLED.getBoolean()) {
            buttons.add(new DisplayButton(MenusLocale.DIVISIONS_ELO_BUTTON_SLOT.getInt(), new ItemBuilder(MenusLocale.DIVISIONS_ELO_BUTTON_MATERIAL.getString()).name(MenusLocale.DIVISIONS_ELO_BUTTON_NAME.getString().replace("<elo>", String.valueOf(API.getProfile(player).getGameData().getGlobalStats().getElo())))
                    .componentLore(ItemUtils.getLore(MenusLocale.DIVISIONS_ELO_BUTTON_LORE.getStringList(), Placeholder.unparsed("elo", String.valueOf(API.getProfile(player).getGameData().getGlobalStats().getElo()))), player)
                    .build()));
        }

        return buttons;
    }

    public ItemStack getItemStack(Player player, Division division) {
        Division playerDivision = DivisionService.get().getDivisionByElo(API.getProfile(player).getGameData().getGlobalStats().getElo());
        ItemBuilder builder = new ItemBuilder(division.getMaterial());
        if (playerDivision.getEloRequired() >= division.getEloRequired()) {
            builder = builder.name(MenusLocale.PASSED_DIVISIONS_ITEM_NAME.getString().replace("<division>", division.getDisplayName()).replace("<elo>", String.valueOf(division.getEloRequired())))
                    .componentLore(ItemUtils.getLore(MenusLocale.PASSED_DIVISIONS_LORE.getStringList(), TagResolver.resolver(
                        Placeholder.parsed("division", division.getDisplayName()), 
                        Placeholder.unparsed("elo", String.valueOf(division.getEloRequired())))), player)
                    .addEnchantedGlow();
        } else {
            builder = builder.name(MenusLocale.DIVISIONS_ITEM_NAME.getString().replace("<division>", division.getDisplayName()).replace("<elo>", String.valueOf(division.getEloRequired())))
                    .componentLore(ItemUtils.getLore(MenusLocale.DIVISIONS_LORE.getStringList(), TagResolver.resolver(
                        Placeholder.parsed("division", division.getDisplayName()),
                        Placeholder.unparsed("elo", String.valueOf(division.getEloRequired())))), player);
        }
        return builder.build();
    }
}
