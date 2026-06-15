package dev.lrxh.neptune.feature.rankedloadout;

import org.bukkit.inventory.ItemStack;

public interface EditableLoadout {
    ItemStack itemAt(int index);

    void setItemAt(int index, ItemStack item);
}
