package dev.lrxh.neptune.feature.cosmetics.menu.armorTrims;

import dev.lrxh.neptune.feature.cosmetics.impl.cosmetics.armortrims.ArmorTrimPackage;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;

public class ArmorTrimDisplayButton extends Button {
    private final boolean selected;
    private final ArmorTrimPackage trimPackage;
    private final ArmorTrim armorTrim;
    private final Material material;
    private final String itemName;
    public ArmorTrimDisplayButton(int slot, boolean selected, ArmorTrimPackage trimPackage, ArmorTrim armorTrim, String materialName, String itemName) {
        super(slot);
        this.selected = selected;
        this.trimPackage = trimPackage;
        this.armorTrim = armorTrim;
        this.material = Registry.MATERIAL.get(Key.key(materialName));
        this.itemName = itemName;
    }
    public ItemStack getItemStack(Player player) {
        ItemBuilder builder = new ItemBuilder(material).name(itemName.replaceAll("<display-name>", trimPackage.getDisplayName()));
        ItemStack stack;
        if (selected) stack = builder.addEnchantedGlow().build();
        else stack = builder.build();
        ItemMeta meta = stack.getItemMeta();
        if (!(meta instanceof ArmorMeta armorMeta)) return stack;
        if (armorTrim != null) armorMeta.setTrim(armorTrim);
        stack.setItemMeta(armorMeta);
        return stack;
    }
}
