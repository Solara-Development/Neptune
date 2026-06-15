package dev.lrxh.neptune.feature.customkit.enchant;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.configs.impl.MessagesLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.Menu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EnchantLevelMenu extends Menu {
    private final LoadoutEnchantContext context;
    private final Enchantment enchantment;

    public EnchantLevelMenu(CustomKit kit, int index, Enchantment enchantment) {
        this(new LoadoutEnchantContext(kit, index, () -> new EnchantmentBrowserMenu(kit, index), () -> {
        }), enchantment);
    }

    public EnchantLevelMenu(LoadoutEnchantContext context, Enchantment enchantment) {
        super(MenusLocale.CUSTOM_KIT_ENCHANT_LEVEL_TITLE.getString(),
                MenusLocale.CUSTOM_KIT_ENCHANT_LEVEL_SIZE.getInt(), Filter.FILL);
        this.context = context;
        this.enchantment = enchantment;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        int max = Math.min(Math.max(enchantment.getMaxLevel(), 5), 9);
        for (int level = 1; level <= max; level++) {
            int lvl = level;
            buttons.add(new Button(level - 1) {
                @Override
                public ItemStack getItemStack(Player p) {
                    return new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                            .name(MenusLocale.CUSTOM_KIT_ENCHANT_LEVEL_NAME.getString().replace("<level>", String.valueOf(lvl)))
                            .amount(lvl).build();
                }

                @Override
                public void onClick(ClickType type, Player p) {
                    ItemStack item = context.getLoadout().itemAt(context.getIndex());
                    if (item == null) {
                        p.closeInventory();
                        return;
                    }
                    item.addUnsafeEnchantment(enchantment, lvl);
                    context.getOnSave().run();
                    Profile profile = API.getProfile(p);
                    if (profile != null) Profile.save(profile);
                    MessagesLocale.CUSTOM_KIT_ENCHANT_APPLIED.send(p.getUniqueId(), TagResolver.resolver(
                            Placeholder.parsed("enchant", EnchantmentBrowserMenu.format(enchantment.getKey().getKey())),
                            Placeholder.unparsed("level", String.valueOf(lvl))));
                    new EnchantmentBrowserMenu(context).open(p);
                }
            });
        }
        buttons.add(new Button(getSize() - 2) {
            @Override
            public ItemStack getItemStack(Player p) {
                return new ItemBuilder(Material.RED_DYE).name(MenusLocale.CUSTOM_KIT_ENCHANT_REMOVE_NAME.getString()).build();
            }

            @Override
            public void onClick(ClickType type, Player p) {
                ItemStack item = context.getLoadout().itemAt(context.getIndex());
                if (item != null) {
                    item.removeEnchantment(enchantment);
                    context.getOnSave().run();
                    Profile profile = API.getProfile(p);
                    if (profile != null) Profile.save(profile);
                }
                new EnchantmentBrowserMenu(context).open(p);
            }
        });
        buttons.add(new ReturnButton(getSize() - 1, new EnchantmentBrowserMenu(context)));
        return buttons;
    }
}
