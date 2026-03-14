package dev.lrxh.api.features.cosmetics.shieldpatterns;

import dev.lrxh.api.features.cosmetics.ICosmeticPackage;
import org.bukkit.block.banner.Pattern;

import java.util.List;

public interface IShieldPatternPackage extends ICosmeticPackage {
    List<Pattern> getPatterns();
    String permission();
}
