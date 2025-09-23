package dev.lrxh.neptune.feature.itembrowser;

import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemBrowserMenu extends PaginatedMenu {

    private final ItemBrowserService service;
    private final String section;
    private final Consumer<Material> itemConsumer;
    private final Runnable returnConsumer;
    private final String search;

    public ItemBrowserMenu(ItemBrowserService service, String section, Consumer<Material> itemConsumer, String search,
                           Runnable returnConsumer) {
        super("&fItem Browser", 54, Filter.NONE);
        this.service = service;
        this.section = section;
        this.itemConsumer = itemConsumer;
        this.search = search == null ? "" : search;
        this.returnConsumer = returnConsumer;
    }

    @Override
    public List<Button> getAllPagesButtons(Player player) {
        List<Material> items = service.getItems(section);
        if (!search.isEmpty()) {
            items = items.stream()
                    .filter(mat -> mat.name().toLowerCase().contains(search.toLowerCase()))
                    .collect(Collectors.toList());
        }
        List<Button> buttons = new ArrayList<>();
        int i = 0;
        for (Material mat : items) {
            if (!mat.isItem())
                continue;
            buttons.add(new Button(i++) {
                @Override
                public ItemStack getItemStack(Player p) {
                    return new ItemBuilder(mat)
                            .name("&f" + mat.name())
                            .build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    itemConsumer.accept(mat);
                    p.closeInventory();
                }
            });
        }
        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> global = new ArrayList<>();
        global.add(new Button(45) {
            @Override
            public ItemStack getItemStack(Player p) {
                return new ItemBuilder(Material.OAK_SIGN)
                        .name("&eSearch")
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
                    p.closeInventory();
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