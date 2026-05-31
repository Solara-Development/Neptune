package dev.lrxh.neptune.feature.customkit.menu;

import dev.lrxh.neptune.API;
import dev.lrxh.neptune.configs.impl.MenusLocale;
import dev.lrxh.neptune.feature.customkit.CustomKit;
import dev.lrxh.neptune.profile.impl.Profile;
import dev.lrxh.neptune.utils.ItemBuilder;
import dev.lrxh.neptune.utils.menu.Button;
import dev.lrxh.neptune.utils.menu.Filter;
import dev.lrxh.neptune.utils.menu.PaginatedMenu;
import dev.lrxh.neptune.utils.menu.impl.ReturnButton;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class CustomKitEffectsMenu extends PaginatedMenu {
    private final CustomKit kit;

    public CustomKitEffectsMenu(CustomKit kit) {
        super(MenusLocale.CUSTOM_KIT_EFFECTS_TITLE.getString(), MenusLocale.CUSTOM_KIT_EFFECTS_SIZE.getInt(), Filter.NONE);
        this.kit = kit;
    }

    private static String format(String key) {
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
        int i = 0;
        for (PotionEffectType type : Registry.EFFECT) {
            buttons.add(new Button(i++) {
                @Override
                public ItemStack getItemStack(Player p) {
                    PotionEffect current = find(type);
                    List<String> lore = new ArrayList<>();
                    lore.add(current != null
                            ? MenusLocale.CUSTOM_KIT_EFFECTS_ENABLED_LORE.getString().replace("<level>", String.valueOf(current.getAmplifier() + 1))
                            : MenusLocale.CUSTOM_KIT_EFFECTS_DISABLED_LORE.getString());
                    lore.add(MenusLocale.CUSTOM_KIT_EFFECTS_TOGGLE_LORE.getString());

                    ItemStack potion = new ItemStack(Material.POTION);
                    if (potion.getItemMeta() instanceof PotionMeta meta) {
                        if (type.getColor() != null) meta.setColor(type.getColor());
                        potion.setItemMeta(meta);
                    }
                    return new ItemBuilder(potion).name(MenusLocale.CUSTOM_KIT_EFFECTS_ITEM_NAME.getString()
                            .replace("<effect>", format(type.getKey().getKey()))).lore(lore).build();
                }

                @Override
                public void onClick(ClickType type2, Player p) {
                    PotionEffect current = find(type);
                    if (type2.isRightClick()) {
                        int amp = current == null ? 0 : (current.getAmplifier() + 1) % (4 + 1);
                        kit.getPotionEffects().remove(current);
                        kit.getPotionEffects().add(new PotionEffect(type, 1_000_000, amp));
                    } else {
                        if (current != null) kit.getPotionEffects().remove(current);
                        else kit.getPotionEffects().add(new PotionEffect(type, 1_000_000, 0));
                    }
                    Profile profile = API.getProfile(p);
                    if (profile != null) Profile.save(profile);
                    open(p);
                }
            });
        }
        return buttons;
    }

    private PotionEffect find(PotionEffectType type) {
        for (PotionEffect effect : kit.getPotionEffects()) if (effect.getType().equals(type)) return effect;
        return null;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new ReturnButton(getSize() - 9, new CustomKitManageMenu(kit)));
        return buttons;
    }

    @Override
    public int getMaxItemsPerPage() {
        return 36;
    }
}
