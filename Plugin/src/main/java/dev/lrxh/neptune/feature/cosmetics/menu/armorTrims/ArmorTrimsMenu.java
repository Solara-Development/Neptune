package dev.lrxh.neptune.feature.cosmetics.menu.armorTrims;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.cosmetics.CosmeticService;
import dev.lrxh.neptune.feature.cosmetics.impl.cosmetics.armortrims.ArmorTrimPackage;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArmorTrimsMenu extends PaginatedMenu {
    public ArmorTrimsMenu() {
        super(MenusLocale.ARMOR_TRIMS_TITLE.getString(), 54, Filter.valueOf(MenusLocale.ARMOR_TRIMS_FILTER.getString()), true);
    }
    public int getMaxItemsPerPage() { return 45; }
    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        ArmorTrimPackage selected = API.getProfile(player).getSettingData().getArmorTrimPackage();
        for (ArmorTrimPackage armorTrimPackage : CosmeticService.get().getArmorTrimPackages().values()) {
            int slot = armorTrimPackage.getSlot();
            int starting = Math.floorDiv(slot, 9) * 36;
            buttons.add(new ArmorTrimButton(starting + slot, armorTrimPackage));
            buttons.add(new ArmorTrimDisplayButton(
                    starting + slot + 9,
                    armorTrimPackage == selected,
                    armorTrimPackage,
                    armorTrimPackage.getHelmetTrim(),
                    MenusLocale.ARMOR_TRIMS_DISPLAY_HELMET_MATERIAL.getString(),
                    armorTrimPackage.getHelmetName()));
            buttons.add(new ArmorTrimDisplayButton(
                    starting + slot + 9 * 2,
                    armorTrimPackage == selected,
                    armorTrimPackage,
                    armorTrimPackage.getChestplateTrim(),
                    MenusLocale.ARMOR_TRIMS_DISPLAY_CHESTPLATE_MATERIAL.getString(),
                    armorTrimPackage.getChestplateName()));
            buttons.add(new ArmorTrimDisplayButton(
                    starting + slot + 9 * 3,
                    armorTrimPackage == selected,
                    armorTrimPackage,
                    armorTrimPackage.getLeggingsTrim(),
                    MenusLocale.ARMOR_TRIMS_DISPLAY_LEGGINGS_MATERIAL.getString(),
                    armorTrimPackage.getLeggingsName()));
            buttons.add(new ArmorTrimDisplayButton(
                    starting + slot + 9 * 4,
                    armorTrimPackage == selected,
                    armorTrimPackage,
                    armorTrimPackage.getBootsTrim(),
                    MenusLocale.ARMOR_TRIMS_DISPLAY_BOOTS_MATERIAL.getString(),
                    armorTrimPackage.getBootsName()));
        }

        return buttons;
    }
}
