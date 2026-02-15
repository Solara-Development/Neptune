package dev.lrxh.neptune.feature.cosmetics.impl;

import dev.lrxh.api.features.cosmetics.ICosmeticPackage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.List;

@Getter
@AllArgsConstructor
public abstract class CosmeticPackage implements ICosmeticPackage {
    protected final String name;
    protected final String displayName;
    protected final Material material;
    protected final List<String> description;
    protected final int slot;

    public abstract String permission();
}