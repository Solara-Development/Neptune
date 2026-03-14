package dev.lrxh.neptune.feature.cosmetics.impl.cosmetics.shieldpatterns;

import dev.lrxh.api.features.cosmetics.shieldpatterns.IShieldPatternPackage;
import dev.lrxh.neptune.feature.cosmetics.impl.CosmeticPackage;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;

import java.util.List;

@Getter
public class ShieldPatternPackage extends CosmeticPackage implements IShieldPatternPackage {
    private final List<Pattern> patterns;

    public ShieldPatternPackage(
            String name,
            String displayName,
            Material material,
            List<String> description,
            int slot,
            List<Pattern> patterns
    ) {
        super(name, displayName, material, description, slot);
        this.patterns = patterns;
    }

    public String permission() {
        return "neptune.cosmetics.killmessages." + name.toLowerCase();
    }
}
