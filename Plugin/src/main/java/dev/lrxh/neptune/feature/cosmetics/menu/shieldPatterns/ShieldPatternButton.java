package dev.lrxh.neptune.feature.cosmetics.menu.shieldPatterns;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.cosmetics.impl.cosmetics.shieldpatterns.ShieldPatternPackage;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.CC;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.ItemUtils;
import dev.lrxh.neptune.utils.menu.Button;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.ShieldMeta;

import java.util.ArrayList;
import java.util.List;

public class ShieldPatternButton extends Button {
    private final ShieldPatternPackage shieldPatternPackage;

    public ShieldPatternButton(int slot, ShieldPatternPackage shieldPatternPackage) {
        super(slot);
        this.shieldPatternPackage = shieldPatternPackage;
    }

    @Override
    public void onClick(ClickType type, Player player) {
        if (!player.hasPermission(shieldPatternPackage.permission())) return;
        API.getProfile(player).getSettingData().setShieldPatternPackage(shieldPatternPackage);
    }

    @Override
    public ItemStack getItemStack(Player player) {
        Profile profile = API.getProfile(player);
        if (profile == null) return null;
        boolean selected = profile.getSettingData().getShieldPatternPackage().equals(shieldPatternPackage);
        List<String> lore;

        if (player.hasPermission(shieldPatternPackage.permission())) {
            lore = selected ? MenusLocale.SHIELD_PATTERNS_SELECTED_LORE.getStringList() : MenusLocale.SHIELD_PATTERNS_UNSELECTED_LORE.getStringList();
        } else {
            lore = MenusLocale.SHIELD_PATTERNS_NO_PERMISSION_LORE.getStringList();
        }
        List<Component> loreToUse = new ArrayList<>();
        for (String line : lore) {
            if (line.contains("<description>")) {
                for (String descLine : shieldPatternPackage.getDescription()) {
                    loreToUse.add(CC.returnMessage(player, line, Placeholder.parsed("description", descLine)));
                }
            }
            else loreToUse.add(CC.returnMessage(player, line));
        }
        Material material = shieldPatternPackage.getMaterial();
        ItemStack stack = new ItemBuilder(material)
                .name(selected ? MenusLocale.SHIELD_PATTERNS_NAME_SELECTED.getString().replace("<display-name>", shieldPatternPackage.getDisplayName()) : MenusLocale.SHIELD_PATTERNS_NAME_NOT_SELECTED.getString().replace("<display-name>", shieldPatternPackage.getDisplayName()))
                .componentLore(ItemUtils.getComponentLore(loreToUse), player)
                .build();
        ItemMeta meta = stack.getItemMeta();
        if (meta instanceof BannerMeta) ((BannerMeta) meta).setPatterns(shieldPatternPackage.getPatterns());
        if (meta instanceof ShieldMeta) ((ShieldMeta) meta).setPatterns(shieldPatternPackage.getPatterns());
        stack.setItemMeta(meta);
        return stack;
    }
}
