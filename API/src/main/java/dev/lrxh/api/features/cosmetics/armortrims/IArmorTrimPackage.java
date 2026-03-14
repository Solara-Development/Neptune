package dev.lrxh.api.features.cosmetics.armortrims;

import dev.lrxh.api.features.cosmetics.ICosmeticPackage;
import org.bukkit.inventory.meta.trim.ArmorTrim;

public interface IArmorTrimPackage extends ICosmeticPackage {
    ArmorTrim getHelmetTrim();
    String getHelmetName();
    ArmorTrim getChestplateTrim();
    String getChestplateName();
    ArmorTrim getLeggingsTrim();
    String getLeggingsName();
    ArmorTrim getBootsTrim();
    String getBootsName();
}
