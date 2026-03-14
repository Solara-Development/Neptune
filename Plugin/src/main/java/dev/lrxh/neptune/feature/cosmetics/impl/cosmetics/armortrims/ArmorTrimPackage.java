package dev.lrxh.neptune.feature.cosmetics.impl.cosmetics.armortrims;

import dev.lrxh.api.features.cosmetics.armortrims.IArmorTrimPackage;
import dev.lrxh.neptune.feature.cosmetics.impl.CosmeticPackage;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.meta.trim.ArmorTrim;

import java.util.List;

@Getter
public class ArmorTrimPackage extends CosmeticPackage implements IArmorTrimPackage {
    private final ArmorTrim helmetTrim;
    private final String helmetName;
    private final ArmorTrim chestplateTrim;
    private final String chestplateName;
    private final ArmorTrim leggingsTrim;
    private final String leggingsName;
    private final ArmorTrim bootsTrim;
    private final String bootsName;

    public ArmorTrimPackage(
            String name,
            String displayName,
            Material material,
            List<String> description,
            int slot,
            ArmorTrim helmetTrim,
            String helmetName,
            ArmorTrim chestplateTrim,
            String chestplateName,
            ArmorTrim leggingsTrim,
            String leggingsName,
            ArmorTrim bootsTrim,
            String bootsName
    ) {
        super(name, displayName, material, description, slot);
        this.helmetTrim = helmetTrim;
        this.helmetName = helmetName;
        this.chestplateTrim = chestplateTrim;
        this.chestplateName = chestplateName;
        this.leggingsTrim = leggingsTrim;
        this.leggingsName = leggingsName;
        this.bootsTrim = bootsTrim;
        this.bootsName = bootsName;
    }

    public String permission() { return "neptune.cosmetics.armortrims." + name.toLowerCase(); }
}
