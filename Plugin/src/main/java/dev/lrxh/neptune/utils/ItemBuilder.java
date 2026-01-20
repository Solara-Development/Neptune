package dev.lrxh.neptune.utils;

import dev.lrxh.neptune.providers.material.NMaterial;
import dev.lrxh.neptune.providers.placeholder.PlaceholderUtil;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemBuilder {
    private final ItemStack item;

    public ItemBuilder(Material material) {
        item = new ItemStack(Objects.requireNonNullElse(material, Material.AIR));
    }

    public ItemBuilder(String material) {
        item = new ItemStack(Objects.requireNonNullElse(Material.valueOf(material), Material.AIR));
    }

    public ItemBuilder(String material, UUID playerUUID) {
        NMaterial nMaterial = null;
        try {
            nMaterial = NMaterial.valueOf(material);
        } catch (IllegalArgumentException ignored) {
        }

        Player player = Bukkit.getPlayer(playerUUID);

        if (nMaterial != null && player != null) {
            item = nMaterial.getItem(player);
        } else if (material != null) {
            item = new ItemStack(Objects.requireNonNullElse(Material.valueOf(material), Material.AIR));
        } else {
            item = new ItemStack(Material.AIR);
        }
    }

    public ItemBuilder(ItemStack itemStack) {
        if (itemStack != null) {
            item = new ItemStack(itemStack);
        } else {
            item = new ItemStack(Material.AIR);
        }
    }

    public ItemBuilder name(String name) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(CC.color(name));
            item.setItemMeta(meta);
        }
        return this;
    }

    private void clearFlags() {
        TooltipDisplay hideAttributes = TooltipDisplay.tooltipDisplay()
                .addHiddenComponents(
                        DataComponentTypes.POTION_CONTENTS,
                        DataComponentTypes.ENCHANTMENTS,
                        DataComponentTypes.ATTRIBUTE_MODIFIERS,
                        DataComponentTypes.DYED_COLOR,
                        DataComponentTypes.TRIM,
                        DataComponentTypes.BANNER_PATTERNS,
                        DataComponentTypes.FIREWORKS,
                        DataComponentTypes.JUKEBOX_PLAYABLE
                ).build();
        item.setData(DataComponentTypes.TOOLTIP_DISPLAY, hideAttributes);
    }

    public ItemBuilder makeUnbreakable() {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(true);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder setCustomModelData(int customData) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(customData);
            item.setItemMeta(meta);
        }
        return this;
    }

    public void resetAmount() {
        item.setAmount(1);
    }

    public ItemBuilder lore(List<String> lore) {
        return lore(lore.toArray(new String[0]));
    }

    public ItemBuilder lore(List<String> lore, Player player) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<TextComponent> toSet = new ArrayList<>();
            for (String string : PlaceholderUtil.format(lore, player)) {
                toSet.add(CC.color(string));
            }
            meta.lore(toSet);
            item.setItemMeta(meta);
        }
        return this;
    }

    public ItemBuilder lore(String... lore) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<TextComponent> toSet = new ArrayList<>();
            for (String string : lore) {
                toSet.add(CC.color(string));
            }
            meta.lore(toSet);
            item.setItemMeta(meta);
        }
        return this;
    }


    public ItemBuilder amount(int amount) {
        item.setAmount(amount <= 0 ? 1 : Math.min(amount, 64));
        return this;
    }
    
    public ItemBuilder addEnchantedGlow() {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            item.setItemMeta(meta);
        }
        return this;
    }
    
    public ItemStack build() {
        clearFlags();
        return item;
    }

}