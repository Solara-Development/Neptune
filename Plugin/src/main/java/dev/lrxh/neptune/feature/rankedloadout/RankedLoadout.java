package dev.lrxh.neptune.feature.rankedloadout;

import dev.lrxh.neptune.utils.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class RankedLoadout implements EditableLoadout {
    public static final int CONTENTS_SIZE = 41;

    private final String id;
    private String displayName;
    private List<ItemStack> items;

    public RankedLoadout(String id, String displayName, List<ItemStack> items) {
        this.id = id;
        this.displayName = displayName;
        this.items = items != null ? new ArrayList<>(items) : defaultTemplate();
    }

    public static List<ItemStack> defaultTemplate() {
        ItemStack[] contents = new ItemStack[CONTENTS_SIZE];
        return new ArrayList<>(Arrays.asList(contents));
    }

    @Override
    public ItemStack itemAt(int index) {
        return index >= 0 && index < items.size() ? items.get(index) : null;
    }

    @Override
    public void setItemAt(int index, ItemStack item) {
        ItemStack[] contents = new ItemStack[Math.max(CONTENTS_SIZE, items.size())];
        for (int i = 0; i < items.size(); i++) contents[i] = items.get(i);
        contents[index] = item;
        items = new ArrayList<>(Arrays.asList(contents));
    }

    public void setItems(List<ItemStack> newItems) {
        this.items = newItems != null ? new ArrayList<>(newItems) : defaultTemplate();
    }

    static RankedLoadout fromStored(String id, String name, String serializedItems) {
        List<ItemStack> loaded = serializedItems == null || serializedItems.isEmpty()
                ? defaultTemplate()
                : ItemUtils.deserialize(serializedItems);
        return new RankedLoadout(id, name, loaded);
    }

    String serializedItems() {
        return ItemUtils.serialize(items);
    }
}
