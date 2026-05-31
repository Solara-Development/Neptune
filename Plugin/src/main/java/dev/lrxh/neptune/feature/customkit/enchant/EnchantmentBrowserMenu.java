package dev.lrxh.neptune.feature.customkit.enchant;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.feature.customkit.menu.CustomKitEditorMenu;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentBrowserMenu extends PaginatedMenu {
    private final CustomKit kit;
    private final int index;

    public EnchantmentBrowserMenu(CustomKit kit, int index) {
        super(MenusLocale.CUSTOM_KIT_ENCHANT_TITLE.getString(), MenusLocale.CUSTOM_KIT_ENCHANT_SIZE.getInt(), Filter.NONE);
        this.kit = kit;
        this.index = index;
    }

    public static String format(String key) {
        String[] words = key.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(' ');
        }
        return sb.toString().trim();
    }

    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        ItemStack item = kit.itemAt(index);
        if (item == null) return buttons;
        int i = 0;
        for (Enchantment enchantment : Registry.ENCHANTMENT) {
            if (!enchantment.canEnchantItem(item)) continue;
            buttons.add(new Button(i++) {
                @Override
                public ItemStack getItemStack(Player p) {
                    int level = item.getEnchantmentLevel(enchantment);
                    String status = level > 0
                            ? MenusLocale.CUSTOM_KIT_ENCHANT_APPLIED_LORE.getString().replace("<level>", String.valueOf(level))
                            : MenusLocale.CUSTOM_KIT_ENCHANT_MAX_LORE.getString().replace("<max>", String.valueOf(enchantment.getMaxLevel()));
                    return new ItemBuilder(Material.ENCHANTED_BOOK)
                            .name(MenusLocale.CUSTOM_KIT_ENCHANT_ITEM_NAME.getString().replace("<enchant>", format(enchantment.getKey().getKey())))
                            .lore(status, MenusLocale.CUSTOM_KIT_ENCHANT_SELECT_LORE.getString()).build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    new EnchantLevelMenu(kit, index, enchantment).open(p);
                }
            });
        }
        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new ReturnButton(getSize() - 9, new CustomKitEditorMenu(kit)));
        return buttons;
    }

    @Override
    public int getMaxItemsPerPage() {
        return 45;
    }
}
