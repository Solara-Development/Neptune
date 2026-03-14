package dev.lrxh.api.features.cosmetics;

import org.bukkit.Material;

import java.util.List;

public interface ICosmeticPackage {
    String getName();
    String getDisplayName();
    Material getMaterial();
    List<String> getDescription();
    int getSlot();
    String permission();
}
