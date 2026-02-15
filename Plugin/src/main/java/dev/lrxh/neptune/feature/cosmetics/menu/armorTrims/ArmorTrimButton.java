package dev.lrxh.neptune.feature.cosmetics.menu.armorTrims;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.cosmetics.impl.armortrims.ArmorTrimPackage;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmorTrimButton extends Button {
    private final ArmorTrimPackage armorTrimPackage;

    public ArmorTrimButton(int slot, ArmorTrimPackage armorTrimPackage) {
        super(slot);
        this.armorTrimPackage = armorTrimPackage;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        if (!player.hasPermission(armorTrimPackage.permission())) return;
        API.getProfile(player).getSettingData().setArmorTrimPackage(armorTrimPackage);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        Profile profile = API.getProfile(player);
        if (profile == null) return null;
        boolean selected = profile.getSettingData().getArmorTrimPackage().equals(armorTrimPackage);
        List<String> lore;

        if (player.hasPermission(armorTrimPackage.permission())) {
            lore = selected ? MenusLocale.ARMOR_TRIMS_SELECTED_LORE.getStringList() : MenusLocale.ARMOR_TRIMS_UNSELECTED_LORE.getStringList();
        } else {
            lore = MenusLocale.ARMOR_TRIMS_NO_PERMISSION_LORE.getStringList();
        }
        List<Component> loreToUse = new ArrayList<>();
        for (String line : lore) {
            if (line.contains("<description>")) {
                for (String descLine : armorTrimPackage.getDescription()) {
                    loreToUse.add(CC.returnMessage(player, line, Placeholder.parsed("description", descLine)));
                }
            }
            else loreToUse.add(CC.returnMessage(player, line));
        }
        return new ItemBuilder(armorTrimPackage.getMaterial())
                .name((selected ? MenusLocale.ARMOR_TRIMS_NAME_SELECTED : MenusLocale.ARMOR_TRIMS_NAME_NOT_SELECTED).getString().replace("<display-name>", armorTrimPackage.getDisplayName()))
                .componentLore(ItemUtils.getComponentLore(loreToUse), player)
                .build();
    }
}
