package dev.lrxh.neptune.feature.itembrowser;

import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ItemBrowserMenu extends PaginatedMenu {

    private final ItemBrowserService service;
    private final String section;
    private final Consumer<ItemStack> itemConsumer;
    private final Runnable returnConsumer;
    private final String search;

    public ItemBrowserMenu(ItemBrowserService service, String section, Consumer<ItemStack> itemConsumer, String search,
                           Runnable returnConsumer) {
        super("&fItem Browser", 54, Filter.NONE);
        this.service = service;
        this.section = section;
        this.itemConsumer = itemConsumer;
        this.search = search == null ? "" : search;
        this.returnConsumer = returnConsumer;
    }

    private static String formatName(String raw) {
        String[] words = raw.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(' ');
        }
        return sb.toString().trim();
    }

    private static String displayName(ItemStack item) {
        String base = formatName(item.getType().name());
        if (item.getItemMeta() instanceof PotionMeta meta && meta.getBasePotionType() != null) {
            return base + " - " + formatName(meta.getBasePotionType().name());
        }
        return base;
    }

    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<ItemStack> items = service.getItemStacks(section);
        if (!search.isEmpty()) {
            String q = search.toLowerCase();
            items = items.stream()
                    .filter(item -> displayName(item).toLowerCase().contains(q)
                            || item.getType().name().toLowerCase().contains(q))
                    .toList();
        }
        List<Button> buttons = new ArrayList<>();
        int i = 0;
        for (ItemStack item : items) {
            if (!item.getType().isItem())
                continue;
            ItemStack stack = item;
            buttons.add(new Button(i++) {
                @Override
                public ItemStack getItemStack(Player p) {
                    return new ItemBuilder(stack.clone())
                            .name("&f" + displayName(stack))
                            .lore(MenusLocale.ITEM_BROWSER_ITEM_LORE.getStringList())
                            .build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    itemConsumer.accept(stack.clone());
                }
            });
        }
        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> global = new ArrayList<>();
        global.add(new Button(MenusLocale.ITEM_BROWSER_SEARCH_SLOT.getInt()) {
            @Override
            public ItemStack getItemStack(Player p) {
                return new ItemBuilder(Material.getMaterial(MenusLocale.ITEM_BROWSER_SEARCH_MATERIAL.getString()))
                        .name(MenusLocale.ITEM_BROWSER_SEARCH_NAME.getString())
                        .build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                service.requestSearch(p, section, itemConsumer, returnConsumer);
            }
        });
        global.add(new Button(49) {
            @Override
            public ItemStack getItemStack(Player p) {
                return new ItemBuilder(Material.ARROW)
                        .name("&cReturn")
                        .build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                if (returnConsumer != null) {
                    returnConsumer.run();
                }
            }
        });
        return global;
    }

    @Override
    public int getMaxItemsPerPage() {
        return 36;
    }
}
